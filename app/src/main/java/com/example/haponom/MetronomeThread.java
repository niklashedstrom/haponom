package com.example.haponom;

import android.media.SoundPool;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class MetronomeThread extends Thread {
    private MetronomeMonitor mm;
    private Vibrator vib;
    private int count;
    private SoundPool sound;


    public MetronomeThread(MetronomeMonitor mm, Vibrator vib){
        this.mm = mm;
        this.vib = vib;
        count = 1;

    }

    public void run(){
        while(true){
            try {
                if (mm.start()) {
                    Thread.sleep(mm.sleeptime());
                    if(count % 4 == 0){
                        count = 1;
                        vib.vibrate(VibrationEffect.createOneShot(mm.vibtime(), VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        count++;
                        vib.vibrate(VibrationEffect.createOneShot(mm.vibtime(), VibrationEffect.DEFAULT_AMPLITUDE));
                    }
                    Thread.sleep(mm.vibtime());
                }
            } catch (InterruptedException e){
                throw new Error(e);
            }
        }
    }
}
