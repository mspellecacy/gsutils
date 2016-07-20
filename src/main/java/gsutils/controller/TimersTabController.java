package gsutils.controller;

import gsutils.core.UserTimedEvent;
import gsutils.gscore.*;
import gsutils.service.GameSenseService;
import gsutils.service.PreferencesService;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Created by mspellecacy on 7/4/2016.
 * NOTES:  So one of the core things with this is that every user timer is effectively a custom gamesense event and that
 * means we have a lot of flexibility in how they're displayed. It also adds another layer of complexity as
 * we're now heavily interacting with the gamesense API as we manage events.
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
        if (prefsService.getUserPrefs().getUserTimedEvents() != null) {
            //Loop over all of our saved events a make sure they're bound.
            ArrayList<UserTimedEvent> events = prefsService.getUserPrefs().getUserTimedEvents();
            for (UserTimedEvent event : events) {
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
            userEvents.add(event);
        }

        //Attach our userEvents to our table.
        userTimedEventsTable.setItems(userEvents);
        userTimedEventsTable.setEditable(true);
        userTimedEventsTable.refresh();

        //Actions Column
        TableColumn<UserTimedEvent, Boolean> actionsColumn = new TableColumn<>();
        //We need a special cell handler since we want to inject buttons.
        class ActionsCell extends TableCell<UserTimedEvent, Boolean> {
            final Button editButton = new Button("", new Glyph("FontAwesome", FontAwesome.Glyph.EDIT));
            final Button delButton = new Button("", new Glyph("FontAwesome", FontAwesome.Glyph.REMOVE));
            final HBox hbox = new HBox();
            final PopOver editor = new PopOver();

            ActionsCell() {
                hbox.getChildren().addAll(editButton, delButton);
                hbox.setSpacing(10);
                hbox.setAlignment(Pos.CENTER);
                delButton.setOnAction(a -> {
                    //TODO: Add confirmation dialog.
                    // get Selected Item
                    UserTimedEvent currentEvent = ActionsCell.this.getTableView().getItems().get(ActionsCell.this.getIndex());
                    //remove selected item from the table list
                    unregisterEvent(currentEvent);
                    userEvents.removeAll(currentEvent);
                });
                editButton.setOnAction(a -> {
                    // get Selected Item
                    UserTimedEvent currentEvent = ActionsCell.this.getTableView().getItems().get(ActionsCell.this.getIndex());
                    editor.setTitle("Event Editor");
                    try {
                        URL editorPath = this.getClass().getClassLoader().getResource("fxml/UserTimedEventEditorPopOver.fxml");
                        FXMLLoader loader = new FXMLLoader(editorPath);
                        UserTimedEventEditorPopOverController controller = new UserTimedEventEditorPopOverController(currentEvent);
                        loader.setController(controller);
                        //controller.initData(currentEvent);

                        editor.setContentNode(loader.load());

                    } catch (IOException e) {
                        log.error("Error loading editor pane: {}", e.getMessage());
                    }
                    editor.show(editButton);
                });
            }

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty)
                    setGraphic(hbox);
            }
        }
        actionsColumn.setCellValueFactory(param -> new SimpleBooleanProperty(true));
        actionsColumn.setCellFactory(param -> new ActionsCell());
        userTimedEventsTable.getColumns().add(actionsColumn);

        gsEventService.setPeriod(Duration.seconds(1));  //Check every second, regardless.

    }

    private void registerEvent(UserTimedEvent event) {
        gsService.bindGameEvent(event.getGameEvent());
    }

    private void unregisterEvent(UserTimedEvent event) {
        GSEventRegistration gsEvent = new GSEventRegistration(event.getGameEvent().getGame(), event.getGameEvent().getEvent());
        gsService.removeGameEvent(gsEvent);
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

    private class GSEventService extends ScheduledService<Void> {
        protected Task<Void> createTask() {
            return new Task<Void>() {
                private void sendEvent(GSBindEvent event) {

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
                    for (UserTimedEvent event : userEvents) {
                        if (event.getEnabled() & LocalDateTime.now().isAfter(event.getNextTriggerDateTime())) {
                            sendEvent(event.getGameEvent());
                            if (event.getAutoRestartTimer()) {
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
}
