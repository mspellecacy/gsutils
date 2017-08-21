package gsutils.controller;

import gsutils.core.UserTimedEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import jfxtras.scene.control.LocalDateTimeTextField;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Created by mspellecacy on 7/19/2016.
 */
class UserTimedEventEditorPopOverController implements Initializable {
    private UserTimedEvent userEvent = new UserTimedEvent();

    @FXML
    private
    TextField eventNameField;

    @FXML
    private
    LocalDateTimeTextField nextTriggerDateTimeField;

    UserTimedEventEditorPopOverController(UserTimedEvent userEvent) {
        this.userEvent = userEvent;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nextTriggerDateTimeField.setDateTimeFormatter(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        updateFields();
    }

    public void initData(UserTimedEvent userEvent) {
        this.userEvent = userEvent;
        updateFields();
    }

    private void updateFields() {
        eventNameField.setText(userEvent.getEventName());

        nextTriggerDateTimeField.setLocalDateTime(userEvent.getNextTriggerDateTime());
    }

}
