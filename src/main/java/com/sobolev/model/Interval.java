package com.sobolev.model;

public class Interval implements Comparable<Interval> {
    private Integer startI;
    private Integer endI;

    public Interval() {
    }

    public Interval(Integer startI, Integer endI) {
        this.startI = startI;
        this.endI = endI;
    }

    public int compareTo(Interval i) {
        if(this.hashcode() < i.hashcode()) return -1;
        if(this.hashCode() > i.hashcode()) return 1;
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
        return String.valueOf(startI) + " " + String.valueOf(endI);
    }

    public boolean equals(Object pair) {
        return pair instanceof Interval && this.toString().equals(pair.toString());
    }

    public int hashcode() {
        return Integer.valueOf(this.toString().replaceAll(" ", ""));
    }
}
