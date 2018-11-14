package UI.controller;

import UI.util.ProcessBar;
import com.jfoenix.controls.JFXRadioButton;
import cpu.CPU;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.annotation.PostConstruct;

@ViewController(value = "/views/body.fxml")
public class BodyController {
    @FXMLViewFlowContext
    private ViewFlowContext context;

    @FXML
    private BorderPane root;

    @FXML
    private Text numOfProcesses, numOfWait, numOfEnd, time;

    @FXML
    private ToggleGroup typeGroup;

    @FXML
    private JFXRadioButton fcfs, rr, srtf, spf, psa;

    @FXML
    private Button start, insert, stop, goON, reset;

    @FXML
    private VBox list1, list2, list3;

    private CPU cpu;

    private static final int TYPE_FCFS = 0;
    private static final int TYPE_RR = 1;
    private static final int TYPE_SRTF = 2;
    private static final int TYPE_SPF = 3;
    private static final int TYPE_PSA = 4;

    @PostConstruct
    public void init() {
        cpu = new CPU();
        bindCPUInfoToControls();
        addProcessBarsTo(cpu, list1);

        start.setOnMouseClicked(event -> {
            new Thread(cpu::start).start();
        });

        insert.setOnMouseClicked(event -> {
            new Thread(cpu::insert).start();
            String pid = cpu.getProcesses().get(cpu.getProcesses().size()-1).getPcb().getPid();
            DoubleProperty doubleProperty = cpu.getProcesses().get(cpu.getProcesses().size()-1).scheduleProperty();
            ProcessBar processBar = new ProcessBar(pid, doubleProperty);
            list1.getChildren().add(processBar);
        });

        stop.setOnMouseClicked(event -> {
            new Thread(cpu::stop).start();
        });

        goON.setOnMouseClicked(event -> {
            new Thread(cpu::goON).start();
        });

        reset.setOnMouseClicked(event -> {
//            new Thread(cpu::reset).start();
            cpu.reset();
            list1.getChildren().clear();
            addProcessBarsTo(cpu, list1);
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

    private void bindCPUInfoToControls(){
        fcfs.setUserData(TYPE_FCFS);
        rr.setUserData(TYPE_RR);
        srtf.setUserData(TYPE_SRTF);
        spf.setUserData(TYPE_SPF);
        psa.setUserData(TYPE_PSA);

        cpu.typeProperty().addListener(((observable, oldValue, newValue) -> {
            Toggle selected = null;
            for (Toggle toggle : typeGroup.getToggles()) {
                if (newValue == toggle.getUserData()){
                    selected = toggle;
                    break;
                }
            }
            if (selected == null){
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
    }

}
