package gsutils.controller;

import gsutils.core.OutputOption;
import gsutils.gscore.*;
import gsutils.monitor.WeatherMonitor;
import gsutils.service.GameSenseService;
import gsutils.service.HostServicesService;
import gsutils.service.PreferencesService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.ResourceBundle;

/**
 * Created by mspellecacy on 6/12/2016.
 */
public class WeatherTabController implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(WeatherTabController.class);

    private final ObservableList<OutputOption> outputOptions = FXCollections.observableArrayList();
    private final PreferencesService prefsService = PreferencesService.INSTANCE;
    private final GameSenseService gsService = GameSenseService.INSTANCE;
    private GSEventService gsEventService = new GSEventService();
    private WeatherUpdateService wus = new WeatherUpdateService();
    private Boolean runMonitor = false;
    private WeatherMonitor weatherMonitor;

    @FXML
    private ComboBox<WeatherMonitor.WeatherUnit> weatherUnits;
    @FXML
    private Hyperlink owmApiKeyLabel;
    @FXML
    private TextField owmApiKeyField;
    @FXML
    private TextField zipcodeField;
    @FXML
    private TableView<OutputOption> weatherStatsTable;
    @FXML
    private ToggleButton toggleServiceButton;
    @FXML
    private TextField outputString;
    @FXML
    private TextField previewOutputString;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.debug("Weather Controller Starting");

        wus.setPeriod(Duration.seconds(10)); //Weather changes relatively slowly, so don't hammer the API
        wus.start();

        gsEventService.setPeriod(Duration.seconds(1));

        owmApiKeyField.setText(prefsService.getUserPrefs().getOpenWeatherMapApiKey());
        weatherUnits.getItems().setAll(WeatherMonitor.WeatherUnit.values());
        weatherUnits.setValue(WeatherMonitor.WeatherUnit.IMPERIAL);
        zipcodeField.setText(prefsService.getUserPrefs().getWeatherZipcodeString());
        weatherStatsTable.setItems(outputOptions);

        if (prefsService.getUserPrefs().getRunWeatherMonitor() != null && prefsService.getUserPrefs().getRunWeatherMonitor())
            toggleServiceButton.fire();

        if (prefsService.getUserPrefs().getWeatherStatsString() != null) {
            outputString.setText(prefsService.getUserPrefs().getWeatherStatsString());
        } else {
            outputString.setText("TEMP_CURF | WEAT_CON");
        }

        registerEvent();
        startMonitor();
    }

    private void registerEvent() {

        //Register the new event Weather provides.
        GSEventRegistration eventReg = new GSEventRegistration();
        eventReg.setGame("GSUTILS");
        eventReg.setEvent("WEATHER");
        gsService.registerGameEvent(eventReg);

        // Now bind event...
        GSBindEvent bindEvent = new GSBindEvent();
        bindEvent.setGame("GSUTILS");
        bindEvent.setEvent("WEATHER");

        //Basic Datas Map...
        ArrayList<HashMap<String, Object>> dataFrames = new ArrayList<>();
        HashMap<String, Object> dataFrame = new HashMap<>();
        dataFrame.put("has-text", true);
        dataFrame.put("repeats", true);
        dataFrames.add(dataFrame);

        //Final assembly of our event handler.
        GSScreenedEventHandler eventHandler = new GSScreenedEventHandler();
        eventHandler.setDatas(dataFrames);

        bindEvent.setEventHandlers(new GSEventHandler[]{eventHandler});
        gsService.bindGameEvent(bindEvent);

    }

    private void startMonitor() {
        if (owmApiKeyField.getText() == "" && prefsService.getUserPrefs().getOpenWeatherMapApiKey() == null) {
            log.info("No default api key found.");

        } else {
            log.info("OWM Api Key: {}", prefsService.getUserPrefs().getOpenWeatherMapApiKey());
            weatherMonitor = new WeatherMonitor(prefsService.getUserPrefs().getOpenWeatherMapApiKey());
            if (runMonitor) gsEventService.restart();
        }


    }

    private void updateWeather() {
        HashMap<String, Object> weatherObj = new HashMap<>();

        if (runMonitor) {

            //Go fetch our Weather data...
            weatherObj = (HashMap<String, Object>) weatherMonitor.getWeatherByZip(zipcodeField.getText(), weatherUnits.getValue());

            //If we got something back from the OWM API...
            if (!weatherObj.isEmpty() && !weatherObj.get("cod").equals(401)) {

                //Cast our returned weather data in to usable objects
                HashMap<String, Object> weatherTemps = (HashMap<String, Object>) weatherObj.get("main");
                HashMap<String, Object> windStats = (HashMap<String, Object>) weatherObj.get("wind");
                //Not really sure why the api treats the 'weather' return values as a Array, but whatever. DOUBLE CASTING!!
                HashMap<String, Object> weatherConds = (HashMap<String, Object>) ((ArrayList<Object>) weatherObj.get("weather")).get(0);

                //Clear out any stale data
                outputOptions.clear();

                //Add all of the variables we wish to provide our user for output.
                outputOptions.setAll(
                        new OutputOption("Weather Conditions",
                                String.valueOf(weatherConds.get("main")),
                                "WEAT_CON"),
                        new OutputOption("Current Temp",
                                String.valueOf(weatherTemps.get("temp")),
                                "TEMP_CUR"),
                        new OutputOption("Day's High",
                                String.valueOf(weatherTemps.get("temp_max")),
                                "TEMP_MAX"),
                        new OutputOption("Day's Low",
                                String.valueOf(weatherTemps.get("temp_min")),
                                "TEMP_MIN"),
                        new OutputOption("Humidity in Percent",
                                String.valueOf(weatherTemps.get("humidity")),
                                "TEMP_HUM"),
                        new OutputOption("Pressure in hPa",
                                String.valueOf(weatherTemps.get("pressure")),
                                "TEMP_PRS"),
                        new OutputOption("Wind Speed",
                                String.valueOf(windStats.get("speed")),
                                "WIND_SPD"),
                        new OutputOption("Wind Dir (in Deg)",
                                String.valueOf(windStats.get("deg")),
                                "WIND_DIR"),
                        new OutputOption("Wind Gust Speed",
                                String.valueOf(windStats.get("gust")),
                                "WIND_GST")
                );  //end .setAll(...)
            }
        }


    }

    private void updatePreviewLabel() {
        String dataValue = outputString.getText();
        for (OutputOption option : outputOptions) {
            dataValue = dataValue.replace(option.getSymbolValue(), option.getCurrentValue());
        }
        previewOutputString.setText(dataValue);
    }

    public void openBrowser(Event event) {
        HostServicesService.INSTANCE.getHostServices().showDocument("http://openweathermap.org/api");
    }

    public void toggleGSEvents(ActionEvent actionEvent) {
        runMonitor = toggleServiceButton.isSelected();
        if (runMonitor) {
            toggleServiceButton.setSelected(true);
            toggleServiceButton.setText("ON");
            gsEventService.restart();
        } else {
            toggleServiceButton.setSelected(false);
            toggleServiceButton.setText("OFF");
            gsEventService.cancel();
        }

        log.info("Weather Monitoring Service Running: " + runMonitor);
    }

    public void savePrefs(ActionEvent actionEvent) {
        prefsService.getUserPrefs().setRunWeatherMonitor(runMonitor);
        prefsService.getUserPrefs().setOpenWeatherMapApiKey(owmApiKeyField.getText());
        prefsService.getUserPrefs().setWeatherStatsString(outputString.getText());
        prefsService.getUserPrefs().setWeatherUnit(weatherUnits.getValue());
        prefsService.getUserPrefs().setWeatherZipcodeString(zipcodeField.getText());
        prefsService.savePreferences();
    }

    public void unitChange(ActionEvent actionEvent) {
        //updateWeather();
    }

    private class WeatherUpdateService extends ScheduledService<Void> {
        protected Task<Void> createTask() {
            return new Task<Void>() {
                protected Void call() throws Exception {
                    updateWeather();
                    updatePreviewLabel();
                    return null;
                }
            };
        }
    }

    private class GSEventService extends ScheduledService<Void> {
        protected Task<Void> createTask() {
            return new Task<Void>() {
                protected Void call() throws Exception {
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
                    gsEvent.setEvent("WEATHER");
                    gsEvent.setData(outputMap);

                    //Push Game Sense event
                    gsService.queueGameEvent(gsEvent, 1000);

                    return null;
                }
            };
        }

    }
}