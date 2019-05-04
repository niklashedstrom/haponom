package com.example.haponom;


public class StepCounterHandler extends Thread {
    LegMechanicMonitor lma;

    public StepCounterHandler(LegMechanicMonitor lma){
        this.lma = lma;
    }

    public void run(){
        while(true){ {
                try {
                    if(lma.startLeg()) {
                        int startSteps = lma.getSteps();
                        Thread.sleep(15000);
                        int resSteps = lma.getSteps() - startSteps;
                        lma.setBPM(resSteps * 4);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
