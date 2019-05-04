package com.example.haponom;

class MetronomeMonitor {
    private long bpm = 0;
    private boolean run = false;

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

    public synchronized long sleeptime(){
        double hits = bpm / 60; //kanske ej funkar
        double res = (1000 - (hits * vibtime())) / hits;
        System.out.println("Sleep: " + res);
        return (long) res;
    }

    public synchronized long vibtime(){
        return 100;
    }


}
