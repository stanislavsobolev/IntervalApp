package com.sobolev.model;

/**
 * Basic class representing interval
 *
 * Includes both model and dao logic, according to task and security requirements
 * there is no need to implement separate model and dao classes
 */
public class Interval implements Comparable<Interval> {
    private Integer startI;
    private Integer endI;

    public Interval() {
    }

    public Interval(Integer startI, Integer endI) {
        this.startI = startI;
        this.endI = endI;
    }

    public Interval(Interval interval) {
        this.startI = interval.getStartI();
        this.endI = interval.getEndI();
    }

    public int compareTo(Interval i) {
        long thisLong = Long.parseLong(this.toString().replaceAll("_", ""));
        long iLong = Long.parseLong(i.toString().replaceAll("_", ""));
        if(thisLong < iLong) return -1;
        if(thisLong > iLong) return 1;
        else return 0;
    }

    public Integer getStartI() {
        return startI;
    }

    public void setStartI(Integer startI) {
        this.startI = startI;
    }

    public Integer getEndI() {
        return endI;
    }

    public void setEndI(Integer endI) {
        this.endI = endI;
    }

    public String toString() {
        return String.valueOf(startI) + "_" + String.valueOf(endI).replaceAll("-", "");
    }

    public boolean equals(Object pair) {
        return pair instanceof Interval && this.toString().equals(pair.toString());
    }

    public int hashcode() {
        return (int) Long.parseLong(this.toString().replaceAll("_", ""));
    }
}
