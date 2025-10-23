package tn.iset.m2glnt.client.viewer.view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import tn.iset.m2glnt.client.viewer.presenter.SlotViewData;

public class SlotView extends VBox {
    private final int slotId;

    public SlotView(SlotViewData slotViewData, Color color) {
        this.slotId = slotViewData.id();
        setBackground(new Background(new BackgroundFill(color, new CornerRadii(3), null)));
        Label label = new Label(slotViewData.description());
        setAlignment(Pos.BASELINE_CENTER);
        getChildren().add(label);
    }

    public int getSlotId() {
        return slotId;
    }


}
