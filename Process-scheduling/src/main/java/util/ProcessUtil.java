package util;

import process.PCB;
import process.Process;

import java.util.ArrayList;
import java.util.List;

public class ProcessUtil {

    public static PCB createPCB() {
        int pid = (int) (Math.random() * 9000 + 1000);
        String pidStr = "#" + Integer.toString(pid);
        String name = "process";
        long time = (long) (Math.random() * 30) + 1;
        long arrive_time = (long) (Math.random() * 20);
        int status = Status.READY;
        int pri = (int) (Math.random() * 10);
        PCB pcb = new PCB(pidStr, name, time, arrive_time, status, pri);
        return pcb;
    }

    public static List<Process> createProcesses(int num) {
        List<Process> processes = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            processes.add(createProcess());
        }
        return processes;
    }

    public static Process createProcess(){
        PCB pcb = createPCB();
        return new Process(pcb, 0);
    }

    public static void sortByNeedTime(List<Process> processes) {
        processes.sort((o1, o2) -> {
            long need_time1 = o1.getPcb().getNeed_time();
            long need_time2 = o2.getPcb().getNeed_time();
            if (need_time1 < need_time2)
                return -1;
            else if (need_time1 == need_time2)
                return 0;
            else
                return 1;
        });
    }

    public static void sortByPri(List<Process> processes) {
        processes.sort((o1, o2) -> {
            int pri1 = o1.getPcb().getPri();
            int pri2 = o2.getPcb().getPri();
            if (pri1 > pri2)
                return -1;
            else if (pri1 == pri2)
                return 0;
            else
                return 1;
        });
    }

    public static void showProcesses(List<Process> processes) {
        for (Process process : processes) {
            System.out.println(process.toString());
        }
    }
}
