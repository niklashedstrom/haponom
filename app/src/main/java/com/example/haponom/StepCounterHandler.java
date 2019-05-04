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
                        Thread.sleep(15000);
                        int resSteps = lma.getSteps() - startSteps;
                        lma.setBPM(resSteps * 4);
                        metronome.setBPM(resSteps * 4);
                        System.out.println("BPM SET");
                        metronome.setBool();
                        System.out.println("BOOL SET");
                        metronome.start();
                        System.out.println("METROOOO");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
