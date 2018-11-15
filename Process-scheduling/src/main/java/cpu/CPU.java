package cpu;

import UI.controller.BodyController;
import javafx.scene.control.TextArea;
import util.ComparatorByPri;
import util.ComparatorByTime;
import util.MyQueue;
import UI.util.ProcessBar;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import process.Process;
import util.Status;

import java.util.*;

import static util.ProcessUtil.*;

public class CPU {
    /**
     * CPU 占用情况
     * 1: 空闲
     * 0: 正被占用
     */
    private static int CPU = 1;
    private int time;
    private List<Process> processes;
    private Collection<Integer> waitQueue;
    private SimpleStringProperty numOfProcesses = new SimpleStringProperty();
    private SimpleStringProperty numOfWait = new SimpleStringProperty();
    private SimpleStringProperty numOfEnd = new SimpleStringProperty();
    private SimpleStringProperty str_time = new SimpleStringProperty();
    private SimpleIntegerProperty type = new SimpleIntegerProperty();
    private TextArea log;
    private int count;
    private boolean flag = true;
    private int front;
    private int running;
    private boolean reset = true;
    private VBox[] lists;

    public CPU(TextArea log, VBox... lists) {
        this.lists = lists;
        this.processes = createProcesses(10);
        showProcesses(processes);
        this.log = log;
        addLog("已初始化十个进程");
        this.count = processes.size();
        this.numOfProcesses.set(Integer.toString(processes.size()));
        this.numOfWait.setValue("0");
        this.numOfEnd.setValue("0");
        this.str_time.setValue("0");
        this.time = 0;
        this.front = 0;
        this.running = 0;
    }

    public CPU() {
        this.processes = createProcesses(10);
        this.count = processes.size();
        this.numOfProcesses.set(Integer.toString(processes.size()));
        this.numOfWait.setValue("0");
        this.numOfEnd.setValue("0");
        this.str_time.setValue("0");
        this.time = 0;
        this.front = 0;
        this.running = 0;
    }


    public void start() {
        this.flag = true;
        if (reset) {
            newWaitQueue();
            reset = false;
        }
        addLog("模拟进程调度开始");
        switch (type.get()) {
            case 0:
                fcfs();
                break;
            case 1:
                rr();
                break;
            case 2:
                srtf();
                break;
            case 3:
                spf();
                break;
            case 4:
                psa();
                break;
        }
    }

    public void insert() {
        int time = this.time;
        Process process = createProcess();
        process.getPcb().setArrive_time(process.getArrive_time() + time);
        addLog(time + " 秒: " + "插入线程" + process.getPid() + " 到达时间" + process.getArrive_time() + "秒");
        processes.add(process);
        this.count += 1;
        numOfProcesses.set(Integer.toString(processes.size()));
    }

    public void stop() {
        this.flag = false;
        addLog("调度暂停");
    }

    public void reset() {
        log.setText("");
        addLog("重置进程池");
        addLog("已初始化十个线程");
        this.processes = createProcesses(10);
        showProcesses(processes);
        this.count = processes.size();
        numOfProcesses.set(Integer.toString(processes.size()));
        numOfWait.setValue("0");
        numOfEnd.setValue("0");
        str_time.setValue("0");
        this.time = 0;
        front = 0;
        running = 0;
        CPU = 1;
        reset = true;
    }

    private void newWaitQueue() {
        switch (type.get()) {
            case 0:
                waitQueue = new LinkedList<>();
                break;
            case 1:
                waitQueue = new LinkedList<>();
                break;
            case 2:
                waitQueue = new MyQueue<>();
                break;
            case 3:
                waitQueue = new MyQueue<>();
                break;
            case 4:
                waitQueue = new MyQueue<>();
                break;
        }
    }

    private void addProcessBar(Process process) {
        Platform.runLater(() -> {
            String pid = process.getPid();
            DoubleProperty doubleProperty = process.scheduleProperty();
            ProcessBar processBar = new ProcessBar(pid, doubleProperty);
            if (BodyController.isIsNotFull_1()) {
                lists[0].getChildren().add(processBar);
                if (lists[0].getChildren().size() == 13)
                    BodyController.setIsNotFull_1(false);
            } else if (BodyController.isIsNotFull_2()) {
                lists[1].getChildren().add(processBar);
                if (lists[1].getChildren().size() == 13)
                    BodyController.setIsNotFull_2(false);
            } else {
                lists[2].getChildren().add(processBar);
            }
        });
    }

    private void removeProcessBar(Process process) {
        Platform.runLater(() -> {
            boolean flag = false;
            int i = 0;
            for (VBox list : lists) {
                for (Node node : list.getChildren()) {
                    if (((ProcessBar) node).getPid().equals(process.getPid())) {
                        list.getChildren().remove(node);
                        if (i == 0) {
                            BodyController.setIsNotFull_1(true);
                        } else if (i == 1) {
                            BodyController.setIsNotFull_2(true);
                        }
                        flag = false;
                        break;
                    }
                }
                i++;
                if (flag)
                    break;
            }
        });
    }

    private void addLog(String str) {
        if (log.getText() != null)
            log.appendText(str + "\n");
        else
            log.setText(str);
        System.out.println(str);
    }


    /**
     * 先来先服务算法
     *
     * @param
     */
    public void fcfs() {
        addLog("-------------FCFS算法-------------");
        if (count <= 0) {
            addLog("无可用进程");
        }
        while (flag) {
            for (int i = 0; i < processes.size(); i++) {
                if (processes.get(i).isReady() && processes.get(i).getArrive_time() == time) {
                    addLog(time + " 秒: " + "进程 " + processes.get(i).getPid() + " 到来 ");
                    addProcessBar(processes.get(i));
                    ((LinkedList<Integer>) waitQueue).offer(i);
                    numOfWait.setValue(Integer.toString(waitQueue.size()));
                }
            }
            if (processes.get(running).isRunnable() && processes.get(running).getTime() == 0) {
                addLog(time + " 秒: " + "进程 " + processes.get(running).getPid() + " 结束运行 ");
                processes.get(running).setStatus(Status.END);
                removeProcessBar(processes.get(running));
                count--;
                numOfEnd.setValue(Integer.toString(processes.size() - count));
                CPU = 1;
            }
            if (count == 0) {
                timePass();
                continue;
            }
            if (CPU == 1 && !waitQueue.isEmpty()) {
                running = ((LinkedList<Integer>) waitQueue).poll();
                numOfWait.setValue(Integer.toString(waitQueue.size()));
                addLog(time + " 秒: " + "进程 " + processes.get(running).getPid() + " 开始运行");
                processes.get(running).setStatus(Status.RUNNABLE);
                processes.get(running).work(1);
                CPU = 0;
            } else if (CPU == 0) {
                processes.get(running).work(1);
            }
            timePass();
        }
        System.out.println("---------------------------------");
        showProcesses(processes);
    }

    /**
     * 时间片轮转算法
     *
     * @param
     * @param
     */
    public void rr() {
        int round = 2;
        addLog("-------------RR算法-------------");
        if (count <= 0) {
            addLog("无可用进程");
        }
        while (flag) {
            for (int i = 0; i < processes.size(); i++) {
                if (processes.get(i).isReady() && processes.get(i).getArrive_time() == time) {
                    addLog(time + " 秒: " + "进程 " + processes.get(i).getPid() + " 到来 ");
                    addProcessBar(processes.get(i));
                    ((LinkedList<Integer>) waitQueue).offer(i);
                    numOfWait.setValue(Integer.toString(waitQueue.size()));
                }
            }
            if ((processes.get(running).isRunnable() || processes.get(running).isWait()) && processes.get(running).getTime() == 0) {
                addLog(time + " 秒: " + "进程 " + processes.get(running).getPid() + " 结束运行 ");
                processes.get(running).setStatus(Status.END);
                removeProcessBar(processes.get(running));
                count--;
                numOfEnd.setValue(Integer.toString(processes.size() - count));
                CPU = 1;

            }
            if (count == 0) {
                timePass();
                continue;
            }

            if (CPU == 1 && !waitQueue.isEmpty()) {
                running = ((LinkedList<Integer>) waitQueue).poll();
                numOfWait.setValue(Integer.toString(waitQueue.size()));
                addLog(time + " 秒: " + "进程 " + processes.get(running).getPid() + " 开始运行 ");
                while (processes.get(running).isOver() && !waitQueue.isEmpty()) {
                    running = ((LinkedList<Integer>) waitQueue).poll();
                    numOfWait.setValue(Integer.toString(waitQueue.size()));
                }
                processes.get(running).setStatus(Status.RUNNABLE);
                processes.get(running).work(1);
                CPU = 0;
            } else if (CPU == 0) {
                processes.get(running).work(1);
                if (time % round == 0) {
                    addLog(time + " 秒: " + "进程 " + processes.get(running).getPid() + " 暂停运行 ");
                    processes.get(running).setStatus(Status.WAIT);
                    ((LinkedList<Integer>) waitQueue).offer(running);
                    numOfWait.setValue(Integer.toString(waitQueue.size()));
                    CPU = 1;
                }
            }
            timePass();

        }
        System.out.println("---------------------------------");
        showProcesses(processes);
    }


    /**
     * 最短剩余时间优先算法
     *
     * @param
     * @param
     */
    public void srtf() {
        addLog("-------------SRTF算法-------------");
        if (count <= 0) {
            addLog("无可用进程");
        }

        while (flag) {
            for (int i = 0; i < processes.size(); i++) {
                if (processes.get(i).isReady() && processes.get(i).getArrive_time() == time) {
                    addLog(time + " 秒: " + "进程 " + processes.get(i).getPid() + " 到来 ");
                    addProcessBar(processes.get(i));
                    ((MyQueue<Integer>) waitQueue).offer(i);
                    numOfWait.setValue(Integer.toString(waitQueue.size()));
                }
            }


            if (processes.get(running).isRunnable() && processes.get(running).getTime() == 0) {
                addLog(time + " 秒: " + "进程 " + processes.get(running).getPid() + " 结束运行 ");
                processes.get(running).setStatus(Status.END);
                removeProcessBar(processes.get(running));
                ((MyQueue) waitQueue).poll();
                numOfWait.setValue(Integer.toString(waitQueue.size()));
                count--;
                numOfEnd.setValue(Integer.toString(processes.size() - count));
                CPU = 1;
            }

            if (count == 0) {
                timePass();
                continue;
            }

            if (waitQueue.size() == 1) {
                front = ((MyQueue<Integer>) waitQueue).peek();
            } else if (waitQueue.size() > 1) {
                ((MyQueue<Integer>) waitQueue).sort(new ComparatorByTime(processes));
                if (!processes.get(front).getPid().equals(processes.get(((MyQueue<Integer>) waitQueue).peek()).getPid())) {
                    if (processes.get(running).isRunnable()) {
                        processes.get(running).setStatus(Status.WAIT);
                        addLog(time + " 秒: " + "进程 " + processes.get(running).getPid() + " 暂停运行 ");
                    }
                    front = ((MyQueue<Integer>) waitQueue).peek();
                    CPU = 1;
                }
            }

            if (CPU == 1 && !waitQueue.isEmpty()) {
                running = front;
                addLog(time + " 秒: " + "进程 " + processes.get(running).getPid() + " 开始运行");
                processes.get(running).setStatus(Status.RUNNABLE);
                processes.get(running).work(1);
                CPU = 0;
            } else if (CPU == 0) {
                processes.get(running).work(1);
            }
            timePass();
        }
        System.out.println("---------------------------------");

        showProcesses(processes);
    }

    /**
     * 最短进程优先算法
     *
     * @param
     * @param
     */
    public void spf() {
        addLog("-------------SPF算法-------------");
        if (count <= 0) {
            addLog("无可用进程");
        }

        while (flag) {
            for (int i = 0; i < processes.size(); i++) {
                if (processes.get(i).isReady() && processes.get(i).getArrive_time() == time) {
                    addLog(time + " 秒: " + "进程 " + processes.get(i).getPid() + " 到来 ");
                    addProcessBar(processes.get(i));
                    ((MyQueue<Integer>) waitQueue).offer(i);
                    numOfWait.setValue(Integer.toString(waitQueue.size()));
                }
            }


            if (processes.get(running).isRunnable() && processes.get(running).getTime() == 0) {
                addLog(time + " 秒: " + "进程 " + processes.get(running).getPid() + " 结束运行 ");
                processes.get(running).setStatus(Status.END);
                removeProcessBar(processes.get(running));
                numOfWait.setValue(Integer.toString(waitQueue.size()));
                count--;
                numOfEnd.setValue(Integer.toString(processes.size() - count));
                CPU = 1;
            }

            if (count == 0) {
                timePass();
                continue;
            }


            if (CPU == 1 && waitQueue.size() >= 2) {
                ((MyQueue<Integer>) waitQueue).sort(new ComparatorByTime(this.processes));
            }

            if (CPU == 1 && !waitQueue.isEmpty()) {
                running = ((MyQueue<Integer>) waitQueue).poll();
                addLog(time + " 秒: " + "进程 " + processes.get(running).getPid() + " 开始运行");
                processes.get(running).setStatus(Status.RUNNABLE);
                processes.get(running).work(1);
                CPU = 0;
            } else if (CPU == 0) {
                processes.get(running).work(1);
            }
            timePass();
        }
        System.out.println("---------------------------------");
        showProcesses(processes);
    }

    /**
     * 优先级算法
     *
     * @param
     * @param
     */
    public void psa() {
        addLog("-------------PSA算法-------------");
        if (count <= 0) {
            addLog("无可用进程");
        }

        while (flag) {
            for (int i = 0; i < processes.size(); i++) {
                if (processes.get(i).isReady() && processes.get(i).getArrive_time() == time) {
                    addLog(time + " 秒: " + "进程 " + processes.get(i).getPid() + " 到来 ");
                    addProcessBar(processes.get(i));
                    ((MyQueue<Integer>) waitQueue).offer(i);
                    numOfWait.setValue(Integer.toString(waitQueue.size()));
                }
            }


            if (processes.get(running).isRunnable() && processes.get(running).getTime() == 0) {
                addLog(time + " 秒: " + "进程 " + processes.get(running).getPid() + " 结束运行 ");
                processes.get(running).setStatus(Status.END);
                removeProcessBar(processes.get(running));
                ((MyQueue<Integer>) waitQueue).poll();
                numOfWait.setValue(Integer.toString(waitQueue.size()));
                count--;
                numOfEnd.setValue(Integer.toString(processes.size() - count));
                CPU = 1;
            }

            if (count == 0) {
                timePass();
                continue;
            }


            if (waitQueue.size() == 1) {
                front = ((MyQueue<Integer>) waitQueue).peek();
            } else if (waitQueue.size() > 1) {
                ((MyQueue<Integer>) waitQueue).sort(new ComparatorByPri(this.processes));
                if (!processes.get(front).getPid().equals(processes.get(((MyQueue<Integer>) waitQueue).peek()).getPid())) {
                    if (processes.get(running).isRunnable()) {
                        processes.get(running).setStatus(Status.WAIT);
                        addLog(time + " 秒: " + "进程 " + processes.get(running).getPid() + " 暂停运行 ");
                    }
                    front = ((MyQueue<Integer>) waitQueue).peek();
                    CPU = 1;
                }
            }

            if (CPU == 1 && !waitQueue.isEmpty()) {
                running = front;
                addLog(time + " 秒: " + "进程 " + processes.get(running).getPid() + " 开始运行");
                processes.get(running).setStatus(Status.RUNNABLE);
                processes.get(running).work(1);
                processes.get(running).setPri(processes.get(running).getPri() - 1);
                CPU = 0;
            } else if (CPU == 0) {
                processes.get(running).work(1);
                processes.get(running).setPri(processes.get(running).getPri() - 1);
            }
            timePass();
        }
        System.out.println("---------------------------------");
        showProcesses(processes);
    }

    private void timePass() {
        time++;
        str_time.setValue(Integer.toString(time));
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getNumOfProcesses() {
        return Integer.parseInt(numOfProcesses.get());
    }

    public SimpleStringProperty numOfProcessesProperty() {
        return numOfProcesses;
    }

    public SimpleStringProperty numOfWaitProperty() {
        return numOfWait;
    }

    public SimpleStringProperty numOfEndProperty() {
        return numOfEnd;
    }

    public SimpleIntegerProperty typeProperty() {
        return type;
    }

    public void setType(int type) {
        this.type.set(type);
    }

    public List<Process> getProcesses() {
        return processes;
    }

    public SimpleStringProperty str_timeProperty() {
        return str_time;
    }

}
