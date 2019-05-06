package com.example.haponom;


public class StepCounterHandler extends Thread {
    LegMechanicMonitor lma;
    MetronomeMonitor metronome;

    public StepCounterHandler(LegMechanicMonitor lma, MetronomeMonitor metronome){
        this.lma = lma;
        this.metronome = metronome;
    }

    public void run(){
        while(true){ {
                try {
                    if(lma.startLeg()) {
                        int startSteps = lma.getSteps();
                        Thread.sleep(10000);
                        int resSteps = lma.getSteps() - startSteps;
                        int bpm = resSteps * 6;
                        lma.setBPM(bpm);
                        metronome.setBPM(bpm);
                        metronome.setBool();
                        metronome.start();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
