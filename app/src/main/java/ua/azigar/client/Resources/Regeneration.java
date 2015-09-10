package ua.azigar.client.Resources;

import android.os.Handler;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Azigar on 09.07.2015.
 * класс для регенерации.
 * каждые 2 сек. добавляет 7 жизни или 1 маны
 */

public class Regeneration extends Thread {

    Timer timer;
    int MAX, FACT, VIP, WHAT, N, seconds = 2;
    Handler H;

    public Regeneration(Handler handler, int max, int fact, int vip, int what){
        this.WHAT = what;
        this.H = handler;
        this.MAX = max;
        this.FACT = fact;
        this.VIP = vip;
        //1 - реген жизни, 2 - реген маны
        if (WHAT == 1) {N = 7;} else { N = 1;}
        if (VIP == 0) { VIP = 1; }
        //вешаем задание таймеру (второй аргумент - через сколько всё начнётся,
        // а третий - интервал, каждые seconds секунд таймер будет запускать метод run())
        timer = new Timer();
        timer.schedule(new RemindTask(), 0, seconds * 1000);
    }

    class RemindTask extends TimerTask {
        public void run() {
            if ((MAX - FACT) < (N * VIP)) {
                FACT = FACT + (MAX - FACT);
                H.sendEmptyMessage(FACT);
            } else {
                FACT = FACT + (N * VIP);
                H.sendEmptyMessage(FACT);
                if ((MAX - FACT) < (N * VIP)) seconds = 1;
            }
            if (MAX == FACT) { timer.cancel(); }
        }
    }

    public void MyStop() {
        timer.cancel();
    }
}
