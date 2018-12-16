package process;

public class PCB {
    private String pid;//进程标识符
    private String name;//进程名字
    private long need_time;//所需运行时间
    private long arrive_time;//进程创建时间
    private int status;//进程状态
    private int pri;//优先级

    public PCB(String pid, String name, long need_time, long arrive_time, int status, int pri) {
        this.pid = pid;
        this.name = name;
        this.need_time = need_time;
        this.arrive_time = arrive_time;
        this.status = status;
        this.pri = pri;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getNeed_time() {
        return need_time;
    }

    public void setNeed_time(long need_time) {
        this.need_time = need_time;
    }

    public long getArrive_time() {
        return arrive_time;
    }

    public void setArrive_time(long arrive_time) {
        this.arrive_time = arrive_time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPri() {
        return pri;
    }

    public void setPri(int pri) {
        this.pri = pri;
    }

    @Override
    public String toString() {
        return "PCB{" +
                "pid='" + pid + '\'' +
                ", name='" + name + '\'' +
                ", need_time=" + need_time +
                ", arrive_time=" + arrive_time +
                ", status=" + status +
                ", pri=" + pri +
                '}';
    }
}
