package tn.iset.m2glnt.client.viewer.presenter;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import tn.iset.m2glnt.client.util.TimeInterval;
import tn.iset.m2glnt.client.viewer.view.ButtonConfiguration;
import tn.iset.m2glnt.client.viewer.view.GridCalendarView;
import tn.iset.m2glnt.client.viewer.view.SlotView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SimpleViewElementFactory implements ViewElementFactory {
    @Override
    public Label createDateLabel(LocalDate date) {
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("E\nMMM d");
        Label label = new Label(date.format(dayFormatter));
        label.setPadding(new Insets(1));
        label.setTextAlignment(TextAlignment.CENTER);
        GridPane.setHalignment(label, HPos.CENTER);
        return label;
    }

    @Override
    public Label createTimeIntervalLabel(TimeInterval timeInterval) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        Label label = new Label(timeInterval.start().format(timeFormatter) + "-" + timeInterval.end().format(timeFormatter));
        GridPane.setHalignment(label, HPos.CENTER);
        return label;
    }

    @Override
    public SlotView createSlotView(SlotViewData slotViewData, Color backgroundColor) {
        return new SlotView(slotViewData, backgroundColor);
    }

    @Override
    public GridCalendarView createGrid(int columns, int rows,
                                       int widthFirstColumn,
                                       int widthSecondToLastColumn,
                                       int heightFirstRow,
                                       int heightSecondToLastRow,
                                       Color linesColor,
                                       Color backgroundColor) {
        GridCalendarView grid = new GridCalendarView();
        grid.setBackground(new Background(new BackgroundFill(linesColor, null, null)));
        grid.setHgap(1);
        grid.setVgap(1);
        ColumnConstraints columnConstraints = new ColumnConstraints(widthFirstColumn);
        grid.getColumnConstraints().add(columnConstraints);
        for(int i = 1; i < columns; i++) {
            ColumnConstraints column = new ColumnConstraints(widthSecondToLastColumn);
            grid.getColumnConstraints().add(column);
        }
        RowConstraints row = new RowConstraints(heightFirstRow);
        grid.getRowConstraints().add(row);
        for(int i = 1; i < rows; i++) {
            row = new RowConstraints(heightSecondToLastRow);
            grid.getRowConstraints().add(row);
        }
        for(int i = 0; i < columns; i++) {
            for(int j = 0; j < rows; j++) {
                Pane child = new Pane();
                child.setBackground(new Background(new BackgroundFill(backgroundColor, null, null)));
                grid.add(child, i, j);
            }
        }
        return grid;
    }

    @Override
    public HBox createButtonBox(List<ButtonConfiguration> buttons) {
        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER);
        for (ButtonConfiguration buttonConfiguration : buttons) {
            Button button = new Button(buttonConfiguration.Label());
            button.setOnMouseClicked(__ -> buttonConfiguration.buttonAction().action());
            hBox.getChildren().add(button);
        }
        return hBox;
    }
}
