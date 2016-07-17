package gsutils.controller;

import gsutils.gscore.*;
import gsutils.service.GameSenseService;
import gsutils.service.HostServicesService;
import gsutils.service.PreferencesService;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by mspellecacy on 6/17/2016.
 */
public class DateTimeTabController implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(DateTimeTabController.class);
    private final PreferencesService prefsService = PreferencesService.INSTANCE;
    private final GameSenseService gsService = GameSenseService.INSTANCE;


    private GSEventService gsEventService = new GSEventService();
    private PreviewUpdateService previewUpdateService = new PreviewUpdateService();

    @FXML
    private TextField dateTimePattern;

    @FXML
    private ToggleButton toggleServiceButton;

    @FXML
    private TextField previewOutputString;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.debug("Date/Time Controller Starting");

        //Register our event.
        registerEvent();

        if (prefsService.getUserPrefs().getRunDatetime() != null && prefsService.getUserPrefs().getRunDatetime())
            toggleServiceButton.fire();

        if (prefsService.getUserPrefs().getDatetimePattern() != null) {
            dateTimePattern.setText(prefsService.getUserPrefs().getDatetimePattern());
        }

        gsEventService.setPeriod(Duration.seconds(1));
        previewUpdateService.setPeriod(Duration.seconds(1));
        previewUpdateService.start();

    }

    private void registerEvent() {
        //Register the new event Date/Time provides.
        GSEventRegistration eventReg = new GSEventRegistration();
        eventReg.setGame("GSUTILS");
        eventReg.setEvent("DATETIME");
        gsService.registerGameEvent(eventReg);

        // Now bind event...
        GSBindEvent bindEvent = new GSBindEvent();
        bindEvent.setGame("GSUTILS");
        bindEvent.setEvent("DATETIME");

        //Basic Datas Map...
        ArrayList<HashMap<String, Object>> dataFrames = new ArrayList<>();
        HashMap<String, Object> dataFrame = new HashMap<>();
        dataFrame.put("has-text", true);
        dataFrame.put("repeats", true);
        dataFrames.add(dataFrame);

        GSScreenedEventHandler eventHandler = new GSScreenedEventHandler();
        eventHandler.setDatas(dataFrames);

        bindEvent.setEventHandlers(new GSEventHandler[]{eventHandler});
        gsService.bindGameEvent(bindEvent);
    }

    private String getDateTimeString() {
        String output = "ERR";
        try {
            LocalDateTime date = LocalDateTime.now();
            String pattern = (dateTimePattern.getText().isEmpty()) ? "dd/MM H:mm:ss" : dateTimePattern.getText();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            output = date.format(formatter);
        } catch (IllegalArgumentException ex) {
            log.error(ex.getMessage());
            output = ex.getMessage();
        }
        return output;
    }

    private void updatePreviewLabel() {
        previewOutputString.setText(getDateTimeString());
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

        log.info("Date/Time Service Running: " + gsEventService.stateProperty());
    }

    public void savePrefs(ActionEvent actionEvent) {
        prefsService.getUserPrefs().setRunDatetime(gsEventService.isRunning());
        prefsService.getUserPrefs().setDatetimePattern(dateTimePattern.getText());

        prefsService.savePreferences();
    }

    public void openBrowser(Event event) {
        HostServicesService.INSTANCE.getHostServices().showDocument("https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#patterns");
    }

    private class PreviewUpdateService extends ScheduledService<Void> {
        protected Task<Void> createTask() {
            return new Task<Void>() {
                protected Void call() throws Exception {
                    Platform.runLater(() -> updatePreviewLabel());
                    return null;
                }
            };
        }
    }

    private class GSEventService extends ScheduledService<Void> {
        protected Task<Void> createTask() {
            return new Task<Void>() {
                protected Void call() throws Exception {
                    log.debug("Pushing GS Events");

                    //Get our output String...
                    String dataValue = getDateTimeString();

                    //Setup a basic Map to push as our 'data' in the event.
                    HashMap<String, Object> outputMap = new HashMap<>();

                    //TODO: Pure fucking hack. GS3 seems to ignore events if their payload value doesn't change?
                    // I've tried repeat:[0|true]  in the event frame, but it didn't seem to work.
                    // I've been fucking with this bug for 3+ days... so today I hacked a fix to move on with my life.
                    // I'm sure its something obvious that I'm overlooking but I stopped caring.
                    // So I've decided to as append a random number of spaces to the end of each text payload.
                    // These spaces wont ever be rendered, and they just 'run off' the end of the OLED, so who cares?
                    int staleValueHack = new Random().nextInt(11);
                    for (int i = 0; i <= staleValueHack; i++) {
                        dataValue = dataValue + " ";
                    }

                    //Pack our data payload
                    outputMap.put("value", dataValue);

                    //Setup our GameSense event
                    GSGameEvent gsEvent = new GSGameEvent();
                    gsEvent.setGame("GSUTILS");
                    gsEvent.setEvent("DATETIME");
                    gsEvent.setData(outputMap);

                    //Push Game Sense event
                    gsService.sendGameEvent(gsEvent);

                    //Update our preview regardless.
                    Platform.runLater(() -> updatePreviewLabel());

                    //Return nothing/Void.
                    return null;
                }
            };
        }
    }

}
