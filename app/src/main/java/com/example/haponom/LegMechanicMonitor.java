package com.example.haponom;

public class LegMechanicMonitor {

    private int bpm = 0;
    private int nbrSteps = 0;
    private boolean isPressed = false;
    private boolean countBool = false;
    private int count = 10;

    public LegMechanicMonitor(){

    }

    public synchronized void press(){
        isPressed = true;
    }

    public synchronized void incStep(){
        nbrSteps++;
        System.out.println("Steps: " + nbrSteps);
    }

    public synchronized int getSteps(){
        return nbrSteps;
    }

    public synchronized void setBPM(int bpm){
        this.bpm = bpm;
    }

    public synchronized boolean startLeg(){
        if(isPressed){
            //isPressed = false;
            return true;
        }
        return false;
    }

    public synchronized void done(){
        isPressed = false;
    }

    public synchronized void setCountdownBool(boolean count){
        System.out.println(count + "---------------------");
        countBool = count;
    }

    public synchronized boolean getCountdownCool(){
        return countBool;
    }

    public synchronized void setCountdown(int count){
        this.count = count;
    }

    public synchronized int getCountdown(){
        return count;
    }

}
