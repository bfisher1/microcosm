package util;

public class PollingTimer {

    private long start;
    private long delay;

    public PollingTimer(long delay) {
        this.delay = delay;
        start = System.currentTimeMillis();
    }

    public boolean resetIfReady() {
        if (ready()) {
            reset();
            return true;
        }
        return false;
    }

    public void reset() {
        start = System.currentTimeMillis();
    }

    public boolean ready() {
        return System.currentTimeMillis() - start >= delay;
    }

}
