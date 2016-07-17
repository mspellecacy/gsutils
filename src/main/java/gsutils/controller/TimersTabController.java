package gsutils.controller;

import gsutils.core.UserTimedEvent;
import gsutils.gscore.*;
import gsutils.service.GameSenseService;
import gsutils.service.PreferencesService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.soap.SOAPBinding;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Created by mspellecacy on 7/4/2016.
 * NOTES:  So one of the core things with this is that every user timer is effectively a custom gamesense event and that
 *         means we have a lot of flexibility in how they're displayed. It also adds another layer of complexity as
 *         we're now heavily interacting with the gamesense API as we manage events.
 */

public class TimersTabController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(TimersTabController.class);
    private static final String GAME = "GSUTILS";

    private final ObservableList<UserTimedEvent> userEvents = FXCollections.observableArrayList();
    private final GameSenseService gsService = GameSenseService.INSTANCE;
    private final PreferencesService prefsService = PreferencesService.INSTANCE;
    private final GSEventService gsEventService = new GSEventService();

    @FXML
    private BorderPane timersBorderPane;

    @FXML
    private ToggleButton toggleServiceButton;

    @FXML
    private TableView userTimedEventsTable;

    @FXML
    private Button addTimerButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(prefsService.getUserPrefs().getUserTimedEvents() != null) {
            //Loop over all of our saved events a make sure they're bound.
            ArrayList<UserTimedEvent> events = prefsService.getUserPrefs().getUserTimedEvents();
            for (UserTimedEvent event: events) {
                log.info("Adding UserTimedEvent:{ eventName: {} }",
                       event.getEventName());
                registerEvent(event);
            }

            //Add all our saved events to the Timers Pool.
            userEvents.setAll(events);
        } else { //We're gonna inject a default event if they don't have any yet.
            log.info("Adding new default event for user...");

            //Create an event.
            GSBindEvent newEvent = new GSBindEvent(GAME, "BASIC_TIMER");

            //Create a new Handler for our Event.
            GSPatternPredefined tactilePattern = new GSPatternPredefined(GSTactilePredefinedPattern.STRONGCLICK_100);

            //And add it to our array of patterns...
            GSPattern[] patterns = new GSPattern[]{tactilePattern};

            //Add those patterns to our new TactileEventHandler
            GSTactileEventHandler eventHandler = new GSTactileEventHandler();
            eventHandler.setPattern(patterns);

            //Attach our handler to our event.
            newEvent.setEventHandlers(new GSEventHandler[]{eventHandler});

            //Create the default UserTimedEvent and attach our new GameSense Event.
            UserTimedEvent event = new UserTimedEvent();
            event.setGameEvent(newEvent);
            event.setNextTriggerDateTime(LocalDateTime.now());
            event.setEnabled(true);
            event.setInterval(10);
            event.setAutoRestartTimer(true);

            //Finally register our event with both the GameSense3 API and internally.
            registerEvent(event);
        }

        //Attach our userEvents to our table.
        userTimedEventsTable.setItems(userEvents);
        userTimedEventsTable.setEditable(true);
        userTimedEventsTable.refresh();

        //Event Name Column ...
        TableColumn<UserTimedEvent, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UserTimedEvent, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<UserTimedEvent, String> param) {
                return new SimpleStringProperty(param.getValue().getEventName());
            }
        });
        userTimedEventsTable.getColumns().add(nameColumn);

        //Next Trigger DateTime Column
        TableColumn<UserTimedEvent, String> nextTriggerColumn = new TableColumn<>("Next Trigger Date/Time");
        nextTriggerColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UserTimedEvent, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<UserTimedEvent, String> param) {
                return new SimpleStringProperty(param.getValue().getNextTriggerDateTime().toString());
            }
        });
        userTimedEventsTable.getColumns().add(nextTriggerColumn);

        //Interval Column
        TableColumn<UserTimedEvent, Number> intervalColumn = new TableColumn<>("Interval (sec)");
        intervalColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UserTimedEvent, Number>, ObservableValue<Number>>() {
            @Override
            public ObservableValue<Number> call(TableColumn.CellDataFeatures<UserTimedEvent, Number> param) {
                return  new SimpleIntegerProperty(param.getValue().getInterval());
            }
        });
        userTimedEventsTable.getColumns().add(intervalColumn);

        //Auto Restart Column...
        TableColumn<UserTimedEvent, Boolean> autoRestartColumn = new TableColumn<>("Auto Restart");
        autoRestartColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UserTimedEvent, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<UserTimedEvent, Boolean> param) {
                return new SimpleBooleanProperty(param.getValue().getAutoRestartTimer());
            }
        });
        userTimedEventsTable.getColumns().add(autoRestartColumn);

        //Enabled Column...
        TableColumn<UserTimedEvent, Boolean> enabledColumn = new TableColumn<>("Enabled");
        enabledColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UserTimedEvent, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<UserTimedEvent, Boolean> param) {
                return new SimpleBooleanProperty(param.getValue().getEnabled());
            }
        });
        userTimedEventsTable.getColumns().add(enabledColumn)
;


        BorderPane.setMargin(timersBorderPane, new Insets(10,10,10,10));
        gsEventService.setPeriod(Duration.seconds(1));  //Check every second, regardless.






    }

    private void registerEvent(UserTimedEvent event) {
        gsService.bindGameEvent(event.getGameEvent());
        userEvents.add(event);
    }

    private class GSEventService extends ScheduledService<Void> {
        protected Task<Void> createTask() {
            return new Task<Void>() {
                private void sendEvent(GSBindEvent event){

                    //Get an always-changing output String...
                    String dataValue = LocalDateTime.now().toString();

                    //Setup a basic Map to push as our 'data' in the event.
                    HashMap<String, Object> outputMap = new HashMap<>();

                    //Pack our data payload
                    outputMap.put("value", dataValue);

                    //Setup our GameSense event
                    GSGameEvent gsEvent = new GSGameEvent(event.getGame(), event.getEvent());
                    gsEvent.setData(outputMap);

                    //Push Game Sense event
                    gsService.sendGameEvent(gsEvent);
                }

                protected Void call() throws Exception {
                    for (UserTimedEvent event: userEvents) {
                        if(event.getEnabled() & LocalDateTime.now().isAfter(event.getNextTriggerDateTime())) {
                            sendEvent(event.getGameEvent());
                            if(event.getAutoRestartTimer()) {
                                event.setNextTriggerDateTime(LocalDateTime.now().plusSeconds(event.getInterval()));
                            } else {
                                event.setEnabled(false);
                            }
                        }
                    }
                    userTimedEventsTable.refresh();
                    return null;
                }
            };
        }
    }

    public void savePrefs(ActionEvent actionEvent) {
        prefsService.getUserPrefs().setRunUserTimedEvents(toggleServiceButton.isSelected());
        prefsService.getUserPrefs().setUserTimedEvents(new ArrayList<>(userEvents));
        prefsService.savePreferences();
    }

    public void toggleGSEvents(ActionEvent actionEvent) {

        if (toggleServiceButton.isSelected()) {
            toggleServiceButton.setSelected(true);
            toggleServiceButton.setText("ON");
            gsEventService.restart();
        } else {
            toggleServiceButton.setSelected(false);
            toggleServiceButton.setText("OFF");
            gsEventService.cancel();
        }

        log.info("Timers Service Running: " + gsEventService.isRunning());
    }
}
