package com.example.haponom;


public class StepCounterHandler extends Thread {
    LegMechanicMonitor lma;
    MetronomeMonitor metronome;
    LegMechanicActivity activity;
    private int bpm;

    public StepCounterHandler(LegMechanicMonitor lma, MetronomeMonitor metronome, LegMechanicActivity activity){
        this.lma = lma;
        this.metronome = metronome;
        this.activity = activity;
    }

    public void run(){
        while(true){ {
                try {
                    if(lma.startLeg()) {
                        System.out.println("Reached...................");
                        lma.setCountdownBool(true);
                        int startSteps = lma.getSteps();
                        Thread.sleep(10000);
                        int resSteps = lma.getSteps() - startSteps;
                        bpm = resSteps * 6;
                        lma.setBPM(bpm);
                        metronome.setBPM(bpm);
                        metronome.setBool();
                        metronome.start();
                        lma.done();
                        lma.setCountdownBool(false);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LegMechanicActivity.bpmOnScreen.setText("BPM: " + bpm);
                            }
                        });

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
