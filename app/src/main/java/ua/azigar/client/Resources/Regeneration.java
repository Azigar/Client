package ua.azigar.client.Resources;

import android.os.Handler;
import android.os.Message;

import java.util.concurrent.TimeUnit;

/**
 * Created by Azigar on 09.07.2015.
 * класс для регенерации.
 * каждые 2 сек. добавляет
 */

public class Regeneration extends Thread {

    Handler h;
    int MAX, FACT, VIP, WHAT, N;
    private boolean stopped = false;

    public Regeneration(Handler handler, int max, int fact, int vip, int what) {
        this.WHAT = what;
        if (WHAT == 1) {N = 7;} else { N = 1;}
        this.h = handler;
        this.MAX = max;
        this.FACT = fact;
        this.VIP = vip;
        if (VIP == 0) { VIP = 1; }
        start();
    }

    public void run() {
        Message msg = new Message();  //создаю меседж-обьект

        while (!isStopped()) {
            if ((MAX - FACT) < (N * VIP)) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                FACT = FACT + (MAX - FACT);
                h.sendEmptyMessage(FACT);
            } else {
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                FACT = FACT + (N * VIP);
                h.sendEmptyMessage(FACT);
            }
            if (MAX == FACT) { MyStop (); }
        }
    }

    public void MyStop () {
        setStopped(true);
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    public boolean isStopped() {
        return stopped;
    }
}
