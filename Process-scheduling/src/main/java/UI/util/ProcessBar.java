package UI.util;

import javafx.beans.property.DoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;

public class ProcessBar extends HBox {
    private Label label;
    private ProgressBar progressBar;

    public ProcessBar(String label, DoubleProperty DoubleProperty) {
        super(5);
        this.label = new Label(label);
        this.progressBar = new ProgressBar();
        this.progressBar.progressProperty().bind(DoubleProperty);
        super.getChildren().addAll(this.label, this.progressBar);
    }
}
