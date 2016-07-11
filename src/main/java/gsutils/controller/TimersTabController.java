package gsutils.controller;

import gsutils.core.UserTimedEvent;
import gsutils.gscore.*;
import gsutils.service.GameSenseService;
import gsutils.service.PreferencesService;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
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
    private ToggleButton toggleServiceButton;

    @FXML
    private TableView userTimedEventsTable;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(prefsService.getUserPrefs().getUserTimedEvents() != null) {
            userEvents.setAll(prefsService.getUserPrefs().getUserTimedEvents());
        } else { //We're gonna inject a default event if they don't have any yet.

            //Create an event.
            GSBindEvent newEvent = new GSBindEvent(GAME, "BASIC_TIMER");

            //Create a new Handler for our Event.
            GSPattern[] patterns = new GSPattern[]{new GSPatternPredefined(GSTactilePredefinedPattern.STRONGCLICK_100)};
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
                    return null;
                }
            };
        }
    }

    public void savePrefs(ActionEvent actionEvent) {
        prefsService.getUserPrefs().setRunUserTimedEvents(toggleServiceButton.isSelected());
        prefsService.getUserPrefs().setUserTimedEvents((ArrayList<UserTimedEvent>) userEvents.stream());
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

        log.info("Timers Service Running: " + gsEventService.stateProperty().getName());
    }
}
