package gsutils.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class IndexController implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(IndexController.class);

    @FXML private TabPane indexPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("Main Index Controller loading.");
        indexPane.getSelectionModel().selectedIndexProperty().addListener((ov, oldTab, newTab) -> {
            //Lambda listens to tab changes.
            //log.debug("Tab Changed.");
        });
    }
}
