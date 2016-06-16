package gsutils.controller;

import gsutils.core.OutputOption;
import gsutils.monitor.HostMonitor;
import gsutils.service.GameSenseService;
import gsutils.service.PreferencesService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mspellecacy on 6/10/2016.
 */
public class SystemStatsTabController implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(SystemStatsTabController.class);

    private static boolean runMonitor = false;

    private final GameSenseService gsService = new GameSenseService();
    private final PreferencesService prefsService = PreferencesService.getInstance();
    private final ObservableList<OutputOption> outputOptions = FXCollections.observableArrayList();

    @FXML
    private TableView systemStatsTable;
    @FXML
    private TextField outputString = new TextField("MEMUSED_PCT");

    @FXML
    private TextField previewOutputString;

    @FXML
    private ToggleButton toggleServiceButton;

    @FXML
    private Button savePrefsButton;

    private class UpdateStatsTask extends TimerTask {

        @Override
        public void run() {
            updateMetrics();
            Platform.runLater(() -> updatePreviewLabel());
        }
    }

    private class PushGSEventTask extends TimerTask {

        @Override
        public void run() {
            if (runMonitor) {
                String dataValue = outputString.getText();

                for (OutputOption option : outputOptions) {
                    dataValue = dataValue.replace(option.getSymbolValue(), option.getCurrentValue());
                }

                String eventJson = "{ \"game\": \"CPU_MONITOR\", \"event\": \"CPULOAD\", \"data\": { \"value\": \"" + dataValue + "\" } }";
                gsService.sendGameEvent(eventJson);

            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.debug("System Monitor Starting");

        Timer timer = new Timer();
        timer.schedule(new UpdateStatsTask(), 0, 1000);
        timer.schedule(new PushGSEventTask(), 0, 1000);

        if (prefsService.getUserPrefs().getSystemStatsString() == null) {
            outputString.setText("MEMUSED_PCT%");
        } else outputString.setText(prefsService.getUserPrefs().getSystemStatsString());

        if(prefsService.getUserPrefs().getSystemStatsOn())
            toggleServiceButton.fire();

        systemStatsTable.setItems(outputOptions);
    }

    public void toggleGSEvents(ActionEvent actionEvent) {
        runMonitor = toggleServiceButton.isSelected();
        if(runMonitor){
            toggleServiceButton.setSelected(true);
            toggleServiceButton.setText("ON");
        } else {
            toggleServiceButton.setSelected(false);
            toggleServiceButton.setText("OFF");
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

    private void updateMetrics() {
        log.debug("System Monitor Looping");
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
            log.debug(e.getMessage());
        }

    }

}
