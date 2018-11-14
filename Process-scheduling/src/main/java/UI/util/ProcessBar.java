package UI.util;

import javafx.beans.property.DoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;

public class ProcessBar extends HBox {
    private String pid;
    private Label label;
    private ProgressBar progressBar;

    public ProcessBar(String pid, DoubleProperty DoubleProperty) {
        super(5);
        this.pid = pid;
        this.label = new Label(pid);
        this.progressBar = new ProgressBar();
        this.progressBar.progressProperty().bind(DoubleProperty);
        super.getChildren().addAll(this.label, this.progressBar);
    }

    public String getPid() {
        return pid;
    }
}
