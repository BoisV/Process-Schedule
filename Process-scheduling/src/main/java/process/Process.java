package process;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import util.Status;

public class Process {
    private PCB pcb;
    private long work_time;
    private DoubleProperty schedule;

    public Process(PCB pcb, long work_time) {
        this.pcb = pcb;
        this.work_time = work_time;
        this.schedule = new SimpleDoubleProperty(0.0);
    }

    public void work(long time) {
        work_time += time;
        schedule.setValue((double) work_time / pcb.getNeed_time());
    }

    public boolean isRunnable() {
        return this.getPcb().getStatus() == Status.RUNNABLE;
    }

    public boolean isOver() {
        return this.getPcb().getStatus() == Status.END;
    }

    public boolean isWait() {
        return this.getPcb().getStatus() == Status.WAIT;
    }


    public boolean isReady() {
        return this.getPcb().getStatus() == Status.READY;
    }

    public long getArrive_time() {
        return this.getPcb().getArrive_time();
    }

    public void setStatus(int status) {
        this.getPcb().setStatus(status);
    }

    public long getTime() {
        return this.pcb.getNeed_time() - this.getWork_time();
    }

    public int getPri() {
        return this.getPcb().getPri();
    }

    public void setPri(int i) {
        this.getPcb().setPri(i);
    }

    public PCB getPcb() {
        return pcb;
    }

    public void setPcb(PCB pcb) {
        this.pcb = pcb;
    }

    public long getWork_time() {
        return work_time;
    }

    public void setWork_time(long work_time) {
        this.work_time = work_time;
    }

    public double getSchedule() {
        return schedule.get();
    }

    public DoubleProperty scheduleProperty() {
        return schedule;
    }

    public void setSchedule(double schedule) {
        this.schedule.set(schedule);
    }

    public String getPid() {
        return this.pcb.getPid();
    }

    @Override
    public String toString() {
        return "Process{" + pcb +
                ", work_time=" + work_time +
                ", schedule=" + schedule +
                '}';
    }
}
