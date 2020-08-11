package cn.hamster3.api.runnable;

import java.util.Calendar;


public abstract class DailyThread extends Thread {
    private boolean stop;

    public DailyThread() {
        stop = false;
    }

    @Override
    @SuppressWarnings("BusyWait")
    public void run() {
        while (!stop) {
            Calendar now = Calendar.getInstance();

            Calendar nextTime = Calendar.getInstance();
            nextTime.set(Calendar.MILLISECOND, 0);
            nextTime.set(Calendar.SECOND, 0);
            nextTime.set(Calendar.MINUTE, 0);
            nextTime.set(Calendar.HOUR_OF_DAY, 24);

            long time = nextTime.getTime().getTime() - now.getTime().getTime();

            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            doTask();

            try {
                Thread.sleep(1000 * 60 * 5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public abstract void doTask();

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }
}
