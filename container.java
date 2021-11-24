public class container {
    private double second;
    private double runtime;
    private int day;
    private int hour;
    private double mem;
    private double cpu;

    public container(double second, double runtime, int day, int hour, double mem, double cpu) {
        this.second = second;
        this.runtime = runtime;
        this.day = day;
        this.hour = hour;
        this.mem = mem;
        this.cpu = cpu;
    }

    public double getSecond() {
        return second;
    }

    public void setSecond(double second) {
        this.second = second;
    }

    public double getRuntime() {
        return runtime;
    }

    public void setRuntime(double runtime) {
        this.runtime = runtime;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public double getMem() {
        return mem;
    }

    public void setMem(double mem) {
        this.mem = mem;
    }

    public double getCpu() {
        return cpu;
    }

    public void setCpu(double cpu) {
        this.cpu = cpu;
    }
}
