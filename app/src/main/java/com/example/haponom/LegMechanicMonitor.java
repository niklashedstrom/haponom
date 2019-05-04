package com.example.haponom;

public class LegMechanicMonitor {

    private int bpm = 0;
    private int nbrSteps = 0;
    private boolean isPressed = false;

    public LegMechanicMonitor(){

    }

    public synchronized void press(){
        isPressed = true;
    }

    public synchronized void incStep(){
        nbrSteps++;
    }

    public synchronized int getSteps(){
        return nbrSteps;
    }

    public synchronized void setBPM(int bpm){
        this.bpm = bpm;
        System.out.println("BPM: " + bpm);
    }


    public synchronized boolean startLeg(){
        if(isPressed){
            isPressed = false;
            return true;
        }
        return false;
    }



}
