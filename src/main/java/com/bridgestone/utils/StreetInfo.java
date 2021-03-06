package com.bridgestone.utils;

/**
 * Created by balmung on 30/08/17.
 */
public class StreetInfo {
    private double speed;   //average mean of the speed of the street
    private int length; //number of edge of the street*/

    public StreetInfo (){

    }

    public StreetInfo(double speed, int length) {
        this.speed = speed;
        this.length = length;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void updateSpeed(double newSpeed){

        this.speed = newSpeed * 1/this.length + this.speed * ((length-1)/length);

    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
