package cn.carbrand.data;

import java.util.concurrent.TimeUnit;
 
/**
 * User: canghailan
 * Date: 11-12-9
 * Time: 下午3:45
 * <pre>
 * RUNNING:
 * startTime              split                        now
 * |<-   getSplitTime()   ->|<-   getTimeFromSplit()   ->|
 * |<-                   getTime()                     ->|
 * <pre/>
 * <pre>
 * STOPPED:
 * startTime                           stop            now
 * |<-           getTime()            ->|
 * <pre/>
 */
public class StopWatch {
    private long startTime;
    private long stopTime;
 
    private State state;
    private boolean split;
 
    public StopWatch() {
        reset();
    }
 
    public void start() {
        if (state == State.UNSTARTED) {
            startTime = System.nanoTime();
            state = State.RUNNING;
            return;
        }
        throw new RuntimeException("Stopwatch already started or stopped.");
    }
 
    public void stop() {
        switch (state) {
            case RUNNING: {
                stopTime = System.nanoTime();
            }
            case SUSPENDED: {
                state = State.STOPPED;
                split = false;
                return;
 
            }
        }
        throw new RuntimeException("Stopwatch is not running.");
    }
 
    public void reset() {
        state = State.UNSTARTED;
        split = false;
    }
 
    public void split() {
        if (state == State.RUNNING) {
            stopTime = System.nanoTime();
            split = true;
            return;
        }
        throw new RuntimeException("Stopwatch is not running.");
    }
 
    public void suspend() {
        if (state == State.RUNNING) {
            stopTime = System.nanoTime();
            state = State.SUSPENDED;
            return;
        }
        throw new RuntimeException("Stopwatch must be running to suspend.");
    }
 
    public void resume() {
        if (state == State.SUSPENDED) {
            startTime += System.nanoTime() - stopTime;
            state = State.RUNNING;
            return;
        }
        throw new RuntimeException("Stopwatch must be suspended to resume.");
    }
 
    public long getTime() {
        return TimeUnit.NANOSECONDS.toMillis(getNanoTime());
    }
 
    public long getNanoTime() {
        switch (state) {
            case RUNNING: {
                return System.nanoTime() - startTime;
            }
            case STOPPED:
            case SUSPENDED: {
                return stopTime - startTime;
            }
            case UNSTARTED: {
                return 0;
            }
        }
        throw new RuntimeException("Should never get here.");
    }
 
    public long getSplitTime() {
        return TimeUnit.NANOSECONDS.toMillis(getSplitNanoTime());
    }
 
 
    public long getSplitNanoTime() {
        if (split) {
            return stopTime - startTime;
        }
        throw new RuntimeException("Stopwatch must be running and split to get the split time.");
    }
 
    public long getTimeFromSplit() {
        return TimeUnit.NANOSECONDS.toMillis(getNanoTimeFromSplit());
    }
 
    public long getNanoTimeFromSplit() {
        if (state == State.RUNNING && split) {
            return System.nanoTime() - stopTime;
        }
        throw new RuntimeException("Stopwatch must be running and split to get the time from split.");
    }
 
    enum State {
        UNSTARTED,
        RUNNING,
        STOPPED,
        SUSPENDED
    }
 
}