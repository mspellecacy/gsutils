package gsutils.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class IndexController implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(IndexController.class);

    @FXML private TabPane indexPane;

    public void sayHello() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        indexPane.getSelectionModel().selectedIndexProperty().addListener((ov, oldTab, newTab) -> {

            log.debug("Tab Changed.");
        });
    }
}
