package gsutils.controller;

import gsutils.gscore.*;
import gsutils.monitor.WeatherMonitor;
import gsutils.service.GameSenseService;
import gsutils.service.PreferencesService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.TimerTask;

/**
 * Created by mspellecacy on 6/12/2016.
 */
public class WeatherTabController implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(WeatherTabController.class);

    private static final String __BIND_EVENT_JSON = "{ \"game\": \"GSUTILS\", \"event\": \"WEATHER\", \"icon_id\": 3, \"handlers\": [ { \"device-type\": \"screened\", \"zone\": \"one\", \"mode\": \"screen\", \"datas\": [ { \"has-text\": true, \"suffix\": \"\" } ] } ] }";

    private final PreferencesService prefsService = PreferencesService.getInstance();
    private final GameSenseService gsService = new GameSenseService();

    private WeatherMonitor weatherMonitor;

    @FXML
    private Label owmApiKeyLabel;

    @FXML
    private TextField owmApiKeyField;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.debug("Weather Controller Starting");
        owmApiKeyLabel.setLabelFor(owmApiKeyField);
        registerEvent();
        startMonitor();
    }

    private void registerEvent() {
        GSEventRegistration eventReg = new GSEventRegistration();
        eventReg.setGame("GSUTILS");
        eventReg.setEvent("WEATHER");
        gsService.registerGameEvent(eventReg);

        // Now bind our event... a lot of this seems redundant, but I blame the API.
        GSBindEvent bindEvent= new GSBindEvent();
        bindEvent.setGame(eventReg.getGame());
        bindEvent.setEvent(eventReg.getEvent());

        //Basic Datas Map...
        HashMap<String, String> datas = new HashMap<>();
        datas.put("has-text", "true");
        datas.put("suffix", "");

        GSScreenedEventHandler eventHandler = new GSScreenedEventHandler();
        eventHandler.setDatas(datas);

        bindEvent.setEventHandlers(new GSEventHandler[]{eventHandler});

        gsService.bindGameEvent(bindEvent);
        //gsService.bindGameEvent(__BIND_EVENT_JSON);
    }

    private void startMonitor() {
        //c255316008602082ca86cee730ded475
        //weatherMonitor = new WeatherMonitor("c255316008602082ca86cee730ded475");
        //log.info(weatherMonitor.getWeatherByZip("99507,us", WeatherMonitor.WeatherUnit.IMPERIAL));

        if(owmApiKeyField.getText() == "" && prefsService.getUserPrefs().getOpenWeatherMapApiKey()==null) {
            log.info("No default api key found.");

        } else {
            weatherMonitor = new WeatherMonitor(prefsService.getUserPrefs().getOpenWeatherMapApiKey());
        }

    }

    private void updateWeather() {

        weatherMonitor.getWeatherByZip("99507", WeatherMonitor.WeatherUnit.IMPERIAL);

    }

    private void updatePreviewLabel() {
    }

    class UpdateStatsTask extends TimerTask {

        @Override
        public void run() {
            updateWeather();
            Platform.runLater(() -> updatePreviewLabel());
        }
    }


}
