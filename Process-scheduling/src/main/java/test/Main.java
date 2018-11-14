package test;

import cpu.CPU;
import process.Process;
import util.ProcessUtil;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Process> processes = ProcessUtil.createProcesses(20);
        CPU cpu = new CPU();
        cpu.rr();
    }
}
