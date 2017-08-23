package gsutils.controller;

import gsutils.core.UserTimedEvent;
import gsutils.gscore.*;
import gsutils.service.GameSenseService;
import gsutils.service.PreferencesService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.util.StringConverter;
import jfxtras.scene.control.LocalDateTimeTextField;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
    private final ObservableList<GSEventHandler> eventEditorTimerHandlers = FXCollections.observableArrayList();

    private final GameSenseService gsService = GameSenseService.INSTANCE;
    private final PreferencesService prefsService = PreferencesService.INSTANCE;
    private final GSEventService gsEventService = new GSEventService();

    private UserTimedEvent selectedUserTimedEvent = new UserTimedEvent();
    private GSEventHandler selectedUserTimedEventHandler;

    @FXML
    private BorderPane timersBorderPane;

    @FXML
    private ToggleButton toggleServiceButton;

    @FXML
    private TableView userTimedEventsTable;


    //@FXML
    //private TitledPane editorActionsPane;

    @FXML
    private Button addTimerButton;

    //Details Pane...
    @FXML
    private MenuItem eventEditorActionsMenuSaveItem;
    @FXML
    private MenuItem eventEditorActionsMenuSaveAsNewItem;
    @FXML
    private MenuItem eventEditorActionsMenuDeleteItem;
    @FXML
    private ToggleButton eventEditorEnabledToggle;
    @FXML
    private ToggleButton eventEditorAutoRestartToggle;
    @FXML
    private TextField eventEditorNameField;
    @FXML
    private LocalDateTimeTextField eventEditorNextTriggerDateTimeField;
    @FXML
    private TextField eventEditorRepeatField;
    @FXML
    private TextField eventEditorIntervalField;
    @FXML
    private TableView eventEditorTimerHandlersTable;
    @FXML
    private VBox handlerEditorVBox;


    @SuppressWarnings("unchecked")
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        if (prefsService.getUserPrefs().getUserTimedEvents() != null) {

            //Fetch our saved events
            ArrayList<UserTimedEvent> events = prefsService.getUserPrefs().getUserTimedEvents();

            //Re-register/Update all our saved events to GameSense Host.
            pushUserTimedEventsToHost(events);

            //Add all our saved events to the Registered Timers Pool.
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

        //Handle Master pane selection...
        //Lifted/adapted from: http://stackoverflow.com/questions/26424769/javafx8-how-to-create-listener-for-selection-of-row-in-tableview
        userTimedEventsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {

            if (newSelection != null) {
                //Get our selected event to edit...
                selectedUserTimedEvent = (UserTimedEvent) newSelection;

                //Populate the 'General' Editor Tab..
                eventEditorEnabledToggle.setSelected(selectedUserTimedEvent.getEnabled());
                eventEditorAutoRestartToggle.setSelected(selectedUserTimedEvent.getAutoRestartTimer());
                eventEditorNameField.setText(selectedUserTimedEvent.getEventName());
                eventEditorNextTriggerDateTimeField.setLocalDateTime(selectedUserTimedEvent.getNextTriggerDateTime());
                eventEditorRepeatField.setText(String.valueOf(selectedUserTimedEvent.getRepeat()));
                eventEditorIntervalField.setText(String.valueOf(selectedUserTimedEvent.getInterval()));
                eventEditorTimerHandlers.setAll(selectedUserTimedEvent.getGameEvent().getEventHandlers());

                //Populate the Handlers Editor Tab...
                eventEditorTimerHandlersTable.setItems(eventEditorTimerHandlers);
                eventEditorTimerHandlersTable.getSelectionModel().selectedItemProperty().addListener((o, oS, nS) -> {
                    handlerEditorVBox.getChildren().clear();

                    if (o.getValue() != null) {
                        selectedUserTimedEventHandler = (GSEventHandler) nS;

                        GSDeviceType handlerType = selectedUserTimedEventHandler.getDeviceType();
                        //TODO: This is... probably bad. I think perhaps I've run down an rabbit hole...
                        switch (handlerType) {
                            case TACTILE:
                                switch (nS.getClass().getSimpleName()) {
                                    case "GSTactileEventHandler":
                                        GSTactileEventHandler teHandler = (GSTactileEventHandler) nS;
                                        System.out.println("Pattern Count: " + teHandler.getPattern().length);
                                        for (GSPattern pattern : teHandler.getPattern()) {
                                            tactilePatternEditorBuilder(pattern);
                                        }
                                }
                        }
                    }
                });
            }
        });

        Glyph saveIcon = new Glyph("FontAwesome", FontAwesome.Glyph.SAVE);
        saveIcon.color(Color.BLACK);
        Glyph saveAsIcon = new Glyph("FontAwesome", FontAwesome.Glyph.PLUS);
        saveAsIcon.color(Color.BLACK);
        Glyph deleteIcon = new Glyph("FontAwesome", FontAwesome.Glyph.REMOVE);
        deleteIcon.color(Color.BLACK);

        eventEditorActionsMenuSaveItem.setGraphic(saveIcon);
        eventEditorActionsMenuSaveAsNewItem.setGraphic(saveAsIcon);
        eventEditorActionsMenuDeleteItem.setGraphic(deleteIcon);

        StringConverter<Integer> formatter = new StringConverter<Integer>() {
            @Override
            public String toString(Integer object) {
                return (object == null) ? "0" : object.toString();
            }

            @Override
            public Integer fromString(String string) {
                return Integer.parseInt(string);
            }
        };

        eventEditorIntervalField.setTextFormatter(new TextFormatter<>(formatter, 0));
        eventEditorRepeatField.setTextFormatter(new TextFormatter<>(formatter, 0));

        gsEventService.setPeriod(Duration.seconds(1));  //Check every second, regardless.

    }

    private void pushUserTimedEventsToHost(ArrayList<UserTimedEvent> events) {
        for (UserTimedEvent event : events) {
            log.info("Pushing UserTimedEvent:{ eventName: {} }", event.getEventName());
            registerEvent(event);
        }
    }

    /****
     * Handler Editor Interface Builders...
     */
    private void tactilePatternEditorBuilder(GSPattern pattern) {

        BorderPane headerBP = new BorderPane();
        Label headerLabel = new Label("Handler Editor");
        headerLabel.setStyle("-fx-font: NORMAL 16 Tahoma;");
        headerBP.setLeft(headerLabel);

        HBox buttonHBox = new HBox();
        buttonHBox.setSpacing(5);
        Button saveHandlerButton = new Button("Save");

        Button saveAsNewHandlerButton = new Button("As New");
        buttonHBox.getChildren().addAll(saveHandlerButton, saveAsNewHandlerButton);
        headerBP.setRight(buttonHBox);

        //ComboBox<GSDeviceType> deviceTypeCbx = new ComboBox<>();
        //deviceTypeCbx.getItems().setAll(GSDeviceType.values());
        //ComboBox<String> patternTypeCbx = new ComboBox<>();
        handlerEditorVBox.getChildren().add(headerBP);

        switch (pattern.getClass().getSimpleName()) {
            case "GSPatternPredefined":
                //Extract out Pattern Info...
                GSPatternPredefined p = (GSPatternPredefined) pattern;
                GSTactilePredefinedPattern tp = ((GSPatternPredefined) pattern).getType();

                //Label...
                Label tpLabel = new Label("Predefined Tactile Pattern");

                //Setup Our ComboBox
                ComboBox<GSTactilePredefinedPattern> patternCbx = new ComboBox<>();
                patternCbx.getItems().setAll(GSTactilePredefinedPattern.values());
                patternCbx.setValue(tp);
                // Add our editor options to the VBox for interaction...
                handlerEditorVBox.getChildren().addAll(tpLabel, patternCbx);

                // Implement our individual save ...
                saveHandlerButton.addEventHandler(ActionEvent.ACTION, (e) -> {

                    p.setType(patternCbx.getValue());
                    GSTactileEventHandler handler = (GSTactileEventHandler) selectedUserTimedEventHandler;
                    handler.setPattern(new GSPattern[]{p});
                });

            default:
                System.out.println("Pattern Class: " + pattern.getClass().getSimpleName());
        }

        saveHandlerButton.addEventHandler(ActionEvent.ACTION, (e) -> {
            GSBindEvent bindEvent = selectedUserTimedEvent.getGameEvent();
            //bindEvent.addEventHandler(selectedUserTimedEventHandler);
            ArrayList<GSEventHandler> tempList = new ArrayList<>(Arrays.asList(bindEvent.getEventHandlers()));
            tempList.removeIf((h) -> h.getDeviceType() == selectedUserTimedEventHandler.getDeviceType());
            tempList.add(selectedUserTimedEventHandler);

            bindEvent.setEventHandlers(tempList.toArray(new GSEventHandler[0]));

            selectedUserTimedEvent = (UserTimedEvent) userTimedEventsTable.getSelectionModel().getSelectedItem();

            //Piggyback on our save event...
            eventEditorActionsMenuSaveItemHandler(new ActionEvent());
        });


    }

    /****
     * GameSense Management
     */
    private void registerEvent(UserTimedEvent event) {
        gsService.bindGameEvent(event.getGameEvent());
    }

    private void unregisterEvent(UserTimedEvent event) {
        GSEventRegistration gsEvent = new GSEventRegistration(event.getGameEvent().getGame(), event.getGameEvent().getEvent());
        gsService.removeGameEvent(gsEvent);
    }

    /****
     * Tab UI/UX Event Handlers...
     ****/
    public void eventEditorActionsMenuSaveItemHandler(ActionEvent actionEvent) {
        if (selectedUserTimedEvent.getGameEvent().equals("") && selectedUserTimedEvent.getNextTriggerDateTime() != null) {
            selectedUserTimedEvent.setEnabled(eventEditorEnabledToggle.isSelected());
            selectedUserTimedEvent.setAutoRestartTimer(eventEditorAutoRestartToggle.isSelected());
            selectedUserTimedEvent.setEventName(eventEditorNameField.getText());
            selectedUserTimedEvent.setNextTriggerDateTime(eventEditorNextTriggerDateTimeField.getLocalDateTime());
            selectedUserTimedEvent.setRepeat(Integer.parseInt(eventEditorRepeatField.getText()));
            selectedUserTimedEvent.setInterval(Integer.parseInt(eventEditorIntervalField.getText()));
            userEvents.set(userTimedEventsTable.getSelectionModel().getSelectedIndex(), selectedUserTimedEvent);
            unregisterEvent(selectedUserTimedEvent);
            registerEvent(selectedUserTimedEvent);
        }
    }

    public void selectedEventActionsMenuSaveAsNewItemHandler(ActionEvent event) {
        event.consume();
        //TODO: Make this ensures true-uniqueness of game event names, otherwise bad things could/will happen.
        if (userEvents.stream().anyMatch(e -> e.getGameEvent().getEvent().equals(eventEditorNameField.getText()))) {
            eventEditorNameField.setText("NEW_" + eventEditorNameField.getText());
        }

        //Register our event with GameSense
        eventEditorActionsMenuSaveItem.fire();
    }

    public void selectedEventActionsMenuDeleteItemHandler(ActionEvent event) {
        unregisterEvent(selectedUserTimedEvent);
        userEvents.removeAll(selectedUserTimedEvent);
    }

    public void savePrefs(ActionEvent actionEvent) {
        prefsService.getUserPrefs().setRunUserTimedEvents(toggleServiceButton.isSelected());
        prefsService.getUserPrefs().setUserTimedEvents(new ArrayList<>(userEvents));
        prefsService.savePreferences();

        //Also push our changes to the remote host.
        pushUserTimedEventsToHost(new ArrayList<>(userEvents));

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

        log.info("Timers Service Status: {}", gsEventService.isRunning());
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
                    /*
                    userEvents.stream()
                            .filter(ue -> (ue.getEnabled() & LocalDateTime.now().isAfter(ue.getNextTriggerDateTime())))
                            .forEach(ue -> {
                                sendEvent(ue.getGameEvent());  //Fire off the event...

                                if (ue.getAutoRestartTimer()) {
                                    ue.setNextTriggerDateTime(LocalDateTime.now().plusSeconds(ue.getInterval()));
                                } else {
                                    ue.setEnabled(false);
                                }
                            });
                    */
                    /*  I've decided to use a Java8 stream() instead of this. Not sure its actually made anything better */
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
