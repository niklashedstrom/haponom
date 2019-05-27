package com.example.haponom;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.SoundPool;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class MetronomeThread extends Thread {
    private MetronomeMonitor mm;
    private Vibrator vib;
    private int count;
    private SoundPool sound;
    private MainActivity.Choice choice;
    private int tick;
    private int tock;
    private CameraManager cm;

    public MetronomeThread(MetronomeMonitor mm, Vibrator vib, SoundPool sound, CameraManager cm, Context context){
        this.mm = mm;
        this.vib = vib;
        this.sound = sound;
        this.cm = cm;
        count = 1;
        choice = MainActivity.Choice.VIBRATION;
        tick = sound.load(context, R.raw.tick, 1);
        tock = sound.load(context, R.raw.tock, 1);
    }

    public void run(){
        while(true){
            try {
                if (mm.start()) {
                    if(choice != mm.getChoice()){
                        choice = mm.getChoice();
                    }
                    Thread.sleep(mm.sleepTime());
                    switch (choice) {
                        case VIBRATION:
                            if(count % 4 == 0){
                                count = 1;
                                vib.vibrate(VibrationEffect.createOneShot(mm.vibTime(), 255));
                            } else {
                                count++;
                                vib.vibrate(VibrationEffect.createOneShot(mm.vibTime(), 100));
                            }
                            break;
                        case LIGHT:
                            cm.setTorchMode(cm.getCameraIdList()[0], true);
                            if(count % 4 == 0){
                                count = 1;
                            } else {
                                count++;
                            }
                            break;
                        case SOUND:
                            if(count % 4 == 0){
                                count = 1;
                                sound.play(tick, 1f, 1f, 1, 0, 1f);
                            } else {
                                count++;
                                sound.play(tock, 1f, 1f, 1, 0, 1f);
                            }

                            break;
                    }

                    Thread.sleep(mm.vibTime());
                    sound.stop(tick);
                    sound.stop(tock);
                    cm.setTorchMode(cm.getCameraIdList()[0], false);

                }
            } catch (InterruptedException | CameraAccessException e){
                throw new Error(e);
            }
        }
    }
}
