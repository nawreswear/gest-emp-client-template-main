package tn.iset.m2glnt.client.viewer.presenter.dialog;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class SlotFormBuilder {
    private Dialog<SlotFormResult> dialog;
    private TextField textField;
    private ComboBox<LocalTime> timePicker;
    private ComboBox<Duration> durationPicker;
    private DatePicker datePicker;
    private int rowIndex;
    private GridPane grid;
    private ButtonType confirmButtonType;
    private ButtonType deleteButtonType;

    public SlotFormBuilder reset(){
        dialog = new Dialog<>();
        buildGrid(dialog);
        resetPickers();
        confirmButtonType = null;
        rowIndex = 0;
        return this;
    }

    private void resetPickers() {
        textField = null;
        timePicker = null;
        datePicker = null;
        durationPicker = null;
    }

    private void buildGrid(Dialog<SlotFormResult> dialog) {
        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        dialog.getDialogPane().setContent(grid);
    }

    public SlotFormBuilder buildTitle(String title) {
        dialog.setTitle(title);
        return this;
    }

    public SlotFormBuilder buildHeader(String headerText) {
        dialog.setHeaderText(headerText);
        return this;
    }

    public SlotFormBuilder buildTextFieldWithPrompt(String description, String promptText){
        textField = new TextField();
        textField.setPromptText(promptText);
        addElementToDialog(description, textField);
        return this;
    }

    public SlotFormBuilder buildTextFieldWithDefaultText(String description, String defaultText){
        textField = new TextField();
        textField.setText(defaultText);
        addElementToDialog(description, textField);
        return this;
    }

    public SlotFormBuilder buildTimePicker(String description, List<LocalTime> possibleHours,
                                           int indexDefaultValue){
        timePicker = new ComboBox<>();
        timePicker.getItems().addAll(possibleHours);
        timePicker.getSelectionModel().select(indexDefaultValue);
        addElementToDialog(description, timePicker);
        return this;
    }

    public SlotFormBuilder buildDatePicker(String description, LocalDate defaultDate){
        datePicker = new DatePicker();
        datePicker.setValue(defaultDate);
        addElementToDialog(description, datePicker);
        return this;
    }

    public SlotFormBuilder buildDurationPicker(String description, List<Duration> possibleDurations,
                                           int indexDefaultValue){
        durationPicker = new ComboBox<>();
        durationPicker.getItems().addAll(possibleDurations);
        durationPicker.getSelectionModel().select(indexDefaultValue);
        durationPicker.setConverter(new DurationStringConverter());
        addElementToDialog(description, durationPicker);
        return this;
    }

    private void addElementToDialog(String description, Node content){
        grid.add(new Label(description), 0, rowIndex);
        grid.add(content, 1, rowIndex);
        rowIndex++;
    }

    public SlotFormBuilder buildDeleteButton(String label){
        deleteButtonType = new ButtonType(label, ButtonBar.ButtonData.OTHER);
        dialog.getDialogPane().getButtonTypes().add(deleteButtonType);
        return this;
    }

    public SlotFormBuilder buildConfirmButton(String label){
        confirmButtonType = new ButtonType(label, ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(confirmButtonType);
        return this;
    }

    public SlotFormBuilder buildCancelButton(){
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        return this;
    }


    public Dialog<SlotFormResult> getDialog() {
        requireSetPickersAndButtons();
        addFormConverter();
        disableConfirmButtonWhenTextFieldIsEmpty();
        return dialog;
    }

    private void disableConfirmButtonWhenTextFieldIsEmpty() {
        Node confirmButton = dialog.getDialogPane().lookupButton(confirmButtonType);
        confirmButton.setDisable(textField.getText().trim().isEmpty());
        textField.textProperty().addListener((__, ___, newValue)
                -> confirmButton.setDisable(newValue.trim().isEmpty()));
    }

    private void requireSetPickersAndButtons() {
        if(textField == null || datePicker == null || timePicker == null
                || durationPicker == null || confirmButtonType == null){
            throw new IllegalArgumentException("All the form elements must be set");
        }
    }

    private void addFormConverter() {
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                SlotFormContent content = new SlotFormContent(timePicker.getValue(), datePicker.getValue(),
                        durationPicker.getValue(), textField.getText());
                return new SlotFormResult(SlotFormAction.CONFIRM, content);
            }
            if(dialogButton == deleteButtonType){
                return new SlotFormResult(SlotFormAction.DELETE, null);
            }
            return new SlotFormResult(SlotFormAction.CANCEL, null);
        });
    }
}
