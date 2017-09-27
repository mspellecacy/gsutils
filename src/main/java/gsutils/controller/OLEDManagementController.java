package gsutils.controller;

import gsutils.service.OLEDRotationService;
import gsutils.service.PreferencesService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by mspellecacy on 9/27/2017.
 */
public class OLEDManagementController implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(OLEDManagementController.class);
    private final OLEDRotationService oledRotationService = OLEDRotationService.INSTANCE;
    private final PreferencesService prefsService = PreferencesService.INSTANCE;

    @FXML
    private Spinner<Integer> rotationIntervalSpinner;

    //NOTE: At 1 second intervals things get really wonk, so putting the lower limit at 2.
    private SpinnerValueFactory.IntegerSpinnerValueFactory intFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 60);

    public void initialize(URL location, ResourceBundle resources) {
        //Setup our int factory...
        intFactory.setAmountToStepBy(1);
        intFactory.setValue(oledRotationService.getIntervalSeconds());

        //Attach our factory to our spinner widget.
        rotationIntervalSpinner.setValueFactory(intFactory);
        rotationIntervalSpinner.setEditable(true);

        //Attach a listener to our spinner. (ty java8 lambadas)
        rotationIntervalSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            log.info("Rotation Interval: {}", rotationIntervalSpinner.getValue());
            updateIntervalValue(rotationIntervalSpinner.getValue());
        });
    }

    private void updateIntervalValue(Integer value) {
        oledRotationService.setIntervalSeconds(value);
    }

    public void savePrefs(ActionEvent actionEvent) {
        prefsService.getUserPrefs().setOledRotationInterval(rotationIntervalSpinner.getValue());
        prefsService.savePreferences();
    }

}
