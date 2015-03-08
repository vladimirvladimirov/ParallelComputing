package com.fmi.parallelcomputing.utils;

/**
 * Created by Dev on 3/7/2015.
 */
public class Interval {

    private int a;
    private int b;

    public Interval(int a, int b) {
        this.a = a;
        this.b = b;
    }

    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }
    public void setA(int a) {
        this.a = a;
    }
    public void setB(int b) {
        this.b = b;
    }
}
