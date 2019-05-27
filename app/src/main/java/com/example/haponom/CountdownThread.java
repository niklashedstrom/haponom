/*

NOT IN USE

package com.example.haponom;

public class CountdownThread extends Thread {
    private LegMechanicMonitor legMechanicMonitor;
    private int count = 10;
    private MainActivity activity;

    public CountdownThread(LegMechanicMonitor legMechanicMonitor, MainActivity activity){
        this.legMechanicMonitor = legMechanicMonitor;
        this.activity = activity;
    }

    public void run(){
        while(true){
            try {
                Thread.sleep(1000);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (legMechanicMonitor.getCountdownCool()) {
                            legMechanicMonitor.setCountdown(count);
                            count--;
                            MainActivity.countdown.setText(Integer.toString(count));
                            if(count == 0) count = 10;
                        }
                    }
                });
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
}
*/