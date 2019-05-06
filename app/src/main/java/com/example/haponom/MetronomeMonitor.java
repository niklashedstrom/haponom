package com.example.haponom;

class MetronomeMonitor {
    private long bpm = 0;
    private boolean run = false;
    private MainActivity.Choice choice = MainActivity.Choice.VIBRATION;

    public MetronomeMonitor(){

    }

    public synchronized void setBPM(int bpm){
        this.bpm = bpm;
    }

    public synchronized void setBool(){
        run = true;
    }

    public synchronized boolean start(){
        if(run){
            return true;
        }
        return false;
    }

    public synchronized void stop(){
        run = false;
    }

    public synchronized long sleepTime(){
        double hits = (double) bpm / 60;
        double res = (1000 - (hits * vibTime())) / hits;
        return (long) res;
    }

    public synchronized long vibTime(){
        return 100;
    }

    public synchronized MainActivity.Choice getChoice(){
        return choice;
    }

    public synchronized void setChoice(MainActivity.Choice choice){
        this.choice = choice;
    }

}
