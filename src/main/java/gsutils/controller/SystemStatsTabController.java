package gsutils.controller;

import gsutils.core.OutputOption;
import gsutils.gscore.*;
import gsutils.monitor.HostMonitor;
import gsutils.service.GameSenseService;
import gsutils.service.OLEDRotationService;
import gsutils.service.PreferencesService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.ResourceBundle;

/**
 * Created by mspellecacy on 6/10/2016.
 */
public class SystemStatsTabController implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(SystemStatsTabController.class);
    private static final String EVENT_NAME = "SYSTEM";
    private static boolean runMonitor = false;

    private final GameSenseService gsService = GameSenseService.INSTANCE;
    private final PreferencesService prefsService = PreferencesService.INSTANCE;
    private final ObservableList<OutputOption> outputOptions = FXCollections.observableArrayList();

    private final StatsUpdateService statsUpdateService = new StatsUpdateService();
    private final GSEventService gsEventService = new GSEventService(); //TODO: This naming convention kludge needs a rethink

    @FXML
    private TableView<OutputOption> systemStatsTable;
    @FXML
    private TextField outputString = new TextField("MEMUSED_PCT");

    @FXML
    private TextField previewOutputString;

    @FXML
    private ToggleButton toggleServiceButton;

    @FXML
    private Button savePrefsButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.debug("System Monitor Starting");
        registerEvent();

        if (prefsService.getUserPrefs().getSystemStatsString() == null) {
            outputString.setText("MEMUSED_PCT%");
        } else outputString.setText(prefsService.getUserPrefs().getSystemStatsString());

        if (prefsService.getUserPrefs().getSystemStatsOn() != null && prefsService.getUserPrefs().getSystemStatsOn())
            toggleServiceButton.fire();

        //Setup our services, each firing once a second...
        statsUpdateService.setPeriod(Duration.seconds(1));

        gsEventService.setPeriod(Duration.seconds(1));
        gsEventService.setOnCancelled(event -> OLEDRotationService.INSTANCE.unregisterGameEvent(EVENT_NAME));
        gsEventService.setOnScheduled(event -> OLEDRotationService.INSTANCE.registerGameEvent(EVENT_NAME));

        systemStatsTable.setItems(outputOptions);
    }

    private void registerEvent() {
        //TODO: Make some factories to clean up all this boilerplate?
        //Register the new event Weather provides.
        GSEventRegistration eventReg = new GSEventRegistration();
        eventReg.setGame("GSUTILS");
        eventReg.setEvent(EVENT_NAME);
        gsService.registerGameEvent(eventReg);

        // Now bind event...
        GSBindEvent bindEvent = new GSBindEvent();
        bindEvent.setGame(eventReg.getGame());
        bindEvent.setEvent(eventReg.getEvent());

        //Basic Datas Map...
        ArrayList<HashMap<String, Object>> datas = new ArrayList<>();
        HashMap<String, Object> datasMap = new HashMap<>();
        datasMap.put("has-text", true);
        datasMap.put("repeats", true);

        datas.add(datasMap);

        //Final assembly of our event handler.
        GSScreenedEventHandler eventHandler = new GSScreenedEventHandler();
        eventHandler.setDatas(datas);

        bindEvent.setEventHandlers(new GSEventHandler[]{eventHandler});
        gsService.bindGameEvent(bindEvent);

    }

    public void toggleGSEvents(ActionEvent actionEvent) {
        runMonitor = toggleServiceButton.isSelected();
        if (runMonitor) {
            toggleServiceButton.setSelected(true);
            toggleServiceButton.setText("ON");
            gsEventService.restart();
            statsUpdateService.restart();
        } else {
            toggleServiceButton.setSelected(false);
            toggleServiceButton.setText("OFF");
            gsEventService.cancel();
            statsUpdateService.cancel();
        }

        log.info("Stats Monitoring Service Running: " + runMonitor);
    }

    public void savePrefs(ActionEvent actionEvent) {
        prefsService.getUserPrefs().setSystemStatsOn(runMonitor);
        prefsService.getUserPrefs().setSystemStatsString(outputString.getText());
        prefsService.savePreferences();
    }

    private void updatePreviewLabel() {
        String dataValue = outputString.getText();
        for (OutputOption option : outputOptions) {
            dataValue = dataValue.replace(option.getSymbolValue(), option.getCurrentValue());
        }
        previewOutputString.setText(dataValue);
    }

    //TODO: Refactor in to using String.valueOf() instead of String.format() pattern. Ex- Weather Controller
    private void updateMetrics() {
        try {
            outputOptions.setAll(
                    //---CPULOAD_PCT
                    new OutputOption("CPU Load Percent",
                            String.format("%3.2f", HostMonitor.getSystemCpuLoad()),
                            "CPULOAD_PCT"),

                    //---MEMUSED_PCT
                    new OutputOption("Memory Used Percent",
                            String.format("%3.2f", HostMonitor.getMemoryUsedPercent()),
                            "MEMUSED_PCT"),

                    //---MEMUSED_GB
                    new OutputOption("Memory Used Gigabytes",
                            String.format("%1.2f", (double) HostMonitor.getMemoryUsed() / 1024 / 1024 / 1024),
                            "MEMUSED_GB"),

                    //---MEMFREE_GB
                    new OutputOption("Memory Free Gigabytes",
                            String.format("%1.2f", (double) HostMonitor.getMemoryFree() / 1024 / 1024 / 1024),
                            "MEMFREE_GB"),

                    //---MEMTTL_GB
                    new OutputOption("Memory Total Gigabytes",
                            String.format("%1.2f", (double) HostMonitor.getTotalMemory() / 1024 / 1024 / 1024),
                            "MEMTTL_GB")
            );
        } catch (Exception e) {
            log.debug("Error updating system stats: {}", e.getMessage());
        }

    }

    private class StatsUpdateService extends ScheduledService<Void> {
        protected Task<Void> createTask() {
            return new Task<Void>() {
                protected Void call() throws Exception {
                    updateMetrics();
                    Platform.runLater(SystemStatsTabController.this::updatePreviewLabel);
                    return null;
                }
            };
        }
    }

    private class GSEventService extends ScheduledService<Void> {
        protected Task<Void> createTask() {
            return new Task<Void>() {
                protected Void call() throws Exception {
                    if (runMonitor) {
                        log.debug("Pushing GS Events");

                        //Get our output string
                        String dataValue = outputString.getText();

                        //Do a basic replace against all of our potential output options
                        for (OutputOption option : outputOptions) {
                            dataValue = dataValue.replace(option.getSymbolValue(), option.getCurrentValue());
                        }

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

                        outputMap.put("value", dataValue);

                        //Setup our GameSense event
                        GSGameEvent gsEvent = new GSGameEvent();
                        gsEvent.setGame("GSUTILS");
                        gsEvent.setEvent(EVENT_NAME);
                        gsEvent.setData(outputMap);

                        //Push Game Sense event
                        OLEDRotationService.INSTANCE.queueOLEDEvent(gsEvent);

                    } else {
                        this.cancel();
                    }

                    //Return nothing/void
                    return null;
                }
            };
        }
    }

}
