package UI.controller;

import UI.util.ProcessBar;
import com.jfoenix.controls.JFXRadioButton;
import cpu.CPU;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.annotation.PostConstruct;

@ViewController(value = "/views/body.fxml")
public class BodyController {
    @FXMLViewFlowContext
    private ViewFlowContext context;

    @FXML
    private Text numOfProcesses, numOfWait, numOfEnd, time;

    @FXML
    private ToggleGroup typeGroup;

    @FXML
    private JFXRadioButton fcfs, rr, srtf, spf, psa;

    @FXML
    private Button start, insert, reset;

    @FXML
    private VBox list1, list2, list3;

    @FXML
    private TextArea log;

    private static boolean isNotFull_1 = true, isNotFull_2 = true;
    private boolean isBegin = false;

    private CPU cpu;
    private static final int TYPE_FCFS = 0;
    private static final int TYPE_RR = 1;
    private static final int TYPE_SRTF = 2;
    private static final int TYPE_SPF = 3;
    private static final int TYPE_PSA = 4;

    @PostConstruct
    public void init() {
        cpu = new CPU(log, list1, list2, list3);
        bindCPUInfoToControls();

        start.setOnMouseClicked(event -> {
            if (!isBegin) {
                isBegin = true;
                new Thread(cpu::start).start();
                start.setText("停止");
                reset.setVisible(false);
            } else {
                isBegin = false;
                new Thread(cpu::stop).start();
                start.setText("运行");
                reset.setVisible(true);
            }
        });

        insert.setOnMouseClicked(event -> {
            Thread thread = new Thread(cpu::insert);
            thread.start();
        });

        reset.setOnMouseClicked(event -> {
            Thread thread = new Thread(cpu::reset);
            thread.start();
            list1.getChildren().clear();
            list2.getChildren().clear();
            list3.getChildren().clear();
            isNotFull_1 = true;
            isNotFull_2 = true;
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private static void addProcessBarsTo(CPU cpu, VBox list1) {
        for (int i = 0; i < cpu.getNumOfProcesses(); i++) {
            String pid = cpu.getProcesses().get(i).getPcb().getPid();
            DoubleProperty doubleProperty = cpu.getProcesses().get(i).scheduleProperty();
            ProcessBar processBar = new ProcessBar(pid, doubleProperty);
            list1.getChildren().add(processBar);
        }
    }

    private void bindCPUInfoToControls() {
        fcfs.setUserData(TYPE_FCFS);
        rr.setUserData(TYPE_RR);
        srtf.setUserData(TYPE_SRTF);
        spf.setUserData(TYPE_SPF);
        psa.setUserData(TYPE_PSA);

        cpu.typeProperty().addListener(((observable, oldValue, newValue) -> {
            Toggle selected = null;
            for (Toggle toggle : typeGroup.getToggles()) {
                if (newValue == toggle.getUserData()) {
                    selected = toggle;
                    break;
                }
            }
            if (selected == null) {
                throw new IllegalArgumentException("CPU info set type value which is not in toggle values");

            }
            typeGroup.selectToggle(selected);
        }));

        typeGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) ->
                cpu.setType((Integer) newValue.getUserData()));

        numOfProcesses.textProperty().bind(cpu.numOfProcessesProperty());
        numOfWait.textProperty().bind(cpu.numOfWaitProperty());
        numOfEnd.textProperty().bind(cpu.numOfEndProperty());
        time.textProperty().bind(cpu.str_timeProperty());
        log.textProperty().addListener((observable, oldValue, newValue) -> {
            log.selectPositionCaret(log.getLength());
            log.deselect();
        });
    }

    public static boolean isIsNotFull_1() {
        return isNotFull_1;
    }

    public static void setIsNotFull_1(boolean isNotFull_1) {
        BodyController.isNotFull_1 = isNotFull_1;
    }

    public static boolean isIsNotFull_2() {
        return isNotFull_2;
    }

    public static void setIsNotFull_2(boolean isNotFull_2) {
        BodyController.isNotFull_2 = isNotFull_2;
    }
}
