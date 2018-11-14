package cpu;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import process.Process;
import util.Status;

import java.util.ArrayList;
import java.util.List;

import static util.ProcessUtil.*;

public class CPU {
    /**
     * CPU 占用情况
     * 1: 空闲
     * 0: 正被占用
     */
    static int CPU = 1;
    private int time = 0;
    private List<Process> processes;
    private List<Integer> waitQueue;
    private SimpleStringProperty numOfProcesses = new SimpleStringProperty();
    private SimpleStringProperty numOfWait = new SimpleStringProperty();
    private SimpleStringProperty numOfEnd = new SimpleStringProperty();
    private SimpleStringProperty str_time = new SimpleStringProperty();
    private SimpleIntegerProperty type = new SimpleIntegerProperty();
    private int count;
    private boolean flag = true;
//    private boolean reset;

    public CPU() {
        this.flag = true;
        this.processes = createProcesses(10);
        this.count = processes.size();
        numOfProcesses.set(Integer.toString(processes.size()));
        numOfWait.setValue("0");
        numOfEnd.setValue("0");
        str_time.setValue("0");
        this.time = 0;
    }


    public void start() {
        this.flag = true;
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
        processes.add(process);
        this.count += 1;
        numOfProcesses.set(Integer.toString(processes.size()));

    }

    public void stop() {
        this.flag = false;
    }

    public void goON() {
        this.flag = true;
    }

    public void reset() {
        this.flag = false;
        this.processes = createProcesses(10);
        this.count = processes.size();
        numOfProcesses.set(Integer.toString(processes.size()));
        numOfWait.setValue("0");
        numOfEnd.setValue("0");
        str_time.setValue("0");
        this.time = 0;
    }


    /**
     * 先来先服务算法
     *
     * @param
     */
    public void fcfs() {
        sortByPri(processes);
        showProcesses(processes);
//        int MAXLEN = processes.size();
        waitQueue = new ArrayList<>();
        int front = 0;
        int tail = 0;
        int running = 0;
        System.out.println("-------------FCFS算法-------------");
        if (count <= 0) {
            System.out.println("无可用进程");
            return;
        }
        while (flag) {
            for (int i = 0; i < processes.size(); i++) {
                if (processes.get(i).isReady() && processes.get(i).getArrive_time() == time) {
                    System.out.println("第 " + time + " 秒: " + "进程 " + processes.get(i).getPcb().getPid() + " 到来 ");
                    waitQueue.add(i);
                    tail = (tail + 1) % processes.size();
                }
            }
            if (processes.get(running).isRunnable() && processes.get(running).getTime() == 0) {
                System.out.println("第 " + time + " 秒: " + "进程 " + processes.get(running).getName() + " 结束运行 ");
                processes.get(running).setStatus(Status.END);
                count--;
                CPU = 1;
            }

            if (CPU == 1 && front != tail) {
                running = waitQueue.get(front);
                front = (front + 1) % processes.size();
                System.out.println("第 " + time + " 秒: " + "进程 " + processes.get(running).getName() + " 开始运行");
                processes.get(running).setStatus(Status.RUNNABLE);
                processes.get(running).work(1);
                CPU = 0;
            } else if (CPU == 0) {
                processes.get(running).work(1);
            }
            timePass();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("---------------------------------");
        showProcesses(processes);
    }

    private void timePass() {
        time++;
        str_time.setValue(Integer.toString(time));
    }

    /**
     * 时间片轮转算法
     *
     * @param
     * @param
     */
    public void rr() {
        sortByPri(processes);
        showProcesses(processes);
        int MAXLEN = processes.size() + 1;
        waitQueue = new ArrayList<>();
        int front = 0;
        int tail = 0;
        int running = 0;
        int round = 2;
        System.out.println("-------------RR算法-------------");
        if (count <= 0) {
            System.out.println("无可用进程");
            return;
        }
        while (count > 0) {
            for (int i = 0; i < processes.size(); i++) {
                if (processes.get(i).isReady() && processes.get(i).getArrive_time() == time) {
                    System.out.println("第 " + time + " 秒: " + "进程 " + processes.get(i).getName() + " 到来 ");
                    if (waitQueue.size() < MAXLEN) {
                        waitQueue.add(i);
                    } else {
                        waitQueue.set(tail, i);
                    }
                    tail = (tail + 1) % MAXLEN;
                }
            }
            if ((processes.get(running).isRunnable() || processes.get(running).isWait()) && processes.get(running).getTime() == 0) {
                System.out.println("第 " + time + " 秒: " + "进程 " + processes.get(running).getName() + " 结束运行 ");
                processes.get(running).setStatus(Status.END);
                count--;
                CPU = 1;
                if (count == 0)
                    break;
            }
            if (CPU == 1) {
                if (front != tail) {
                    running = waitQueue.get(front);
                    front = (front + 1) % MAXLEN;
                    System.out.println("第 " + time + " 秒: " + "进程 " + processes.get(running).getName() + " 开始运行 ");
                    while (processes.get(running).isOver()) {
                        running = waitQueue.get(front);
                        front = (front + 1) % MAXLEN;
                    }
                    processes.get(running).setStatus(Status.RUNNABLE);
                    processes.get(running).work(1);
                    CPU = 0;
                }
            } else if (CPU == 0) {
                processes.get(running).work(1);
                if (time % round == 0) {
                    System.out.println("第 " + time + " 秒: " + "进程 " + processes.get(running).getName() + " 暂停运行 ");
                    processes.get(running).setStatus(Status.WAIT);
                    if (waitQueue.size() < MAXLEN) {
                        waitQueue.add(running);
                    } else {
                        waitQueue.set(tail, running);
                    }
                    tail = (tail + 1) % MAXLEN;
                    CPU = 1;
                }
            }
            timePass();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
        sortByNeedTime(processes);
        showProcesses(processes);
        waitQueue = new ArrayList<>();
        int front = 0;
        int running = 0;
        System.out.println("-------------SRTF算法-------------");
        if (count <= 0) {
            System.out.println("无可用进程");
            return;
        }

        while (count > 0) {
            for (int i = 0; i < processes.size(); i++) {
                if (processes.get(i).isReady() && processes.get(i).getArrive_time() == time) {
                    System.out.println("第 " + time + " 秒: " + "进程 " + processes.get(i).getName() + " 到来 ");
                    waitQueue.add(i);
                }
            }


            if (processes.get(running).isRunnable() && processes.get(running).getTime() == 0) {
                System.out.println("第 " + time + " 秒: " + "进程 " + processes.get(running).getName() + " 结束运行 ");
                processes.get(running).setStatus(Status.END);
                waitQueue.remove(0);
                count--;
                CPU = 1;
            }


            if (waitQueue.size() == 1) {
                front = waitQueue.get(0);
            } else if (waitQueue.size() > 1) {
                waitQueue.sort((o1, o2) -> {
                    if (processes.get(o1).getTime() < processes.get(o2).getTime())
                        return -1;
                    else if (processes.get(o1).getTime() == processes.get(o2).getTime())
                        return 0;
                    else
                        return 1;
                });
                if (front != waitQueue.get(0)) {
                    if (processes.get(running).isRunnable()) {
                        processes.get(running).setStatus(Status.WAIT);
                        System.out.println("第 " + time + " 秒: " + "进程 " + processes.get(running).getName() + " 暂停运行 ");
                    }
                    front = waitQueue.get(0);
                    CPU = 1;
                }
            }

            if (CPU == 1 && !waitQueue.isEmpty()) {
                running = front;
                System.out.println("第 " + time + " 秒: " + "进程 " + processes.get(running).getName() + " 开始运行");
                processes.get(running).setStatus(Status.RUNNABLE);
                processes.get(running).work(1);
                CPU = 0;
            } else if (CPU == 0) {
                processes.get(running).work(1);
            }
            timePass();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
        sortByNeedTime(processes);
        showProcesses(processes);
        waitQueue = new ArrayList<>();
        int running = 0;
        System.out.println("-------------SPF算法-------------");
        if (count <= 0) {
            System.out.println("无可用进程");
            return;
        }

        while (count > 0) {
            for (int i = 0; i < processes.size(); i++) {
                if (processes.get(i).isReady() && processes.get(i).getArrive_time() == time) {
                    System.out.println("第 " + time + " 秒: " + "进程 " + processes.get(i).getName() + " 到来 ");
                    waitQueue.add(i);
                }
            }


            if (processes.get(running).isRunnable() && processes.get(running).getTime() == 0) {
                System.out.println("第 " + time + " 秒: " + "进程 " + processes.get(running).getName() + " 结束运行 ");
                processes.get(running).setStatus(Status.END);
                waitQueue.remove(0);
                count--;
                CPU = 1;
            }


            if (CPU == 1 && waitQueue.size() >= 2) {
                waitQueue.sort((o1, o2) -> {
                    if (processes.get(o1).getTime() < processes.get(o2).getTime())
                        return -1;
                    else if (processes.get(o1).getTime() == processes.get(o2).getTime())
                        return 0;
                    else
                        return 1;
                });
            }

            if (CPU == 1 && !waitQueue.isEmpty()) {
                running = waitQueue.get(0);
                System.out.println("第 " + time + " 秒: " + "进程 " + processes.get(running).getName() + " 开始运行");
                processes.get(running).setStatus(Status.RUNNABLE);
                processes.get(running).work(1);
                CPU = 0;
            } else if (CPU == 0) {
                processes.get(running).work(1);
            }
            timePass();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
        sortByPri(processes);
        showProcesses(processes);
        waitQueue = new ArrayList<>();
        int front = 0;
        int running = 0;
        System.out.println("-------------PSA算法-------------");
        if (count <= 0) {
            System.out.println("无可用进程");
            return;
        }

        while (count > 0) {
            for (int i = 0; i < processes.size(); i++) {
                if (processes.get(i).isReady() && processes.get(i).getArrive_time() == time) {
                    System.out.println("第 " + time + " 秒: " + "进程 " + processes.get(i).getName() + " 到来 ");
                    waitQueue.add(i);
                }
            }


            if (processes.get(running).isRunnable() && processes.get(running).getTime() == 0) {
                System.out.println("第 " + time + " 秒: " + "进程 " + processes.get(running).getName() + " 结束运行 ");
                processes.get(running).setStatus(Status.END);
                waitQueue.remove(0);
                count--;
                CPU = 1;
            }


            if (waitQueue.size() == 1) {
                front = waitQueue.get(0);
            } else if (waitQueue.size() > 1) {
                waitQueue.sort((o1, o2) -> {
                    if (processes.get(o1).getPri() > processes.get(o2).getPri())
                        return -1;
                    else if (processes.get(o1).getPri() == processes.get(o2).getPri())
                        return 0;
                    else
                        return 1;
                });
                if (front != waitQueue.get(0)) {
                    if (processes.get(running).isRunnable()) {
                        processes.get(running).setStatus(Status.WAIT);
                        System.out.println("第 " + time + " 秒: " + "进程 " + processes.get(running).getName() + " 暂停运行 ");
                    }
                    front = waitQueue.get(0);
                    CPU = 1;
                }
            }

            if (CPU == 1 && !waitQueue.isEmpty()) {
                running = front;
                System.out.println("第 " + time + " 秒: " + "进程 " + processes.get(running).getName() + " 开始运行");
                processes.get(running).setStatus(Status.RUNNABLE);
                processes.get(running).work(1);
                processes.get(running).setPri(processes.get(running).getPri() - 1);
                CPU = 0;
            } else if (CPU == 0) {
                processes.get(running).work(1);
            }
            timePass();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("---------------------------------");
        showProcesses(processes);
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
