package com.example.haponom;


public class StepCounterHandler extends Thread {
    LegMechanicMonitor lma;
    MetronomeMonitor metronome;
    private int bpm;
    MainActivity activity;

    public StepCounterHandler(LegMechanicMonitor lma, MetronomeMonitor metronome, MainActivity activity){
        this.lma = lma;
        this.metronome = metronome;
        this.activity = activity;
    }

    public void run(){
        while(true){ {
                try {
                    if(lma.startLeg()) {
                        Thread.sleep(1500);
                        int startSteps = lma.getSteps();
                        Thread.sleep(10000);
                        final int resSteps = lma.getSteps() - startSteps;
                        if(resSteps != 0) {
                            bpm = resSteps * 6;
                            lma.setBPM(bpm);
                            metronome.setBPM(bpm);
                            metronome.setBool();
                            metronome.start();
                            activity.bpm = bpm;
                        }
                        lma.done();
                        System.out.println("BPM: " + bpm);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(resSteps != 0) {
                                    activity.BPMText.setText(Integer.toString(bpm));
                                    activity.bpmButton.setImageResource(R.drawable.pause);
                                    activity.flag = true;
                                }
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
