package seedu.vms.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import seedu.vms.logic.commands.CommandResult;

public class ResultMessageBox extends UiPart<Region> {
    private static final String FXML_FILE = "ResultMessageBox.fxml";

    private static final String STYLE_CLASS_ERROR = "result-message-error-color";
    private static final String STYLE_CLASS_WARNING = "result-message-warning-color";
    private static final String STYLE_CLASS_INFO = "result-message-info-color";

    @FXML private Label stateLabel;
    @FXML private Label messageLabel;


    public ResultMessageBox(CommandResult result) {
        super(FXML_FILE);
        stateLabel.setText(String.format("[%s]", result.getState().toString()));
        messageLabel.setText(result.getMessage());
        String colorStyleClass;
        switch (result.getState()) {

        case ERROR:
            colorStyleClass = STYLE_CLASS_ERROR;
            break;

        case WARNING:
            colorStyleClass = STYLE_CLASS_WARNING;
            break;

        default:
            colorStyleClass = STYLE_CLASS_INFO;
            break;

        }
        stateLabel.getStyleClass().add(colorStyleClass);
        messageLabel.getStyleClass().add(colorStyleClass);
    }
}
