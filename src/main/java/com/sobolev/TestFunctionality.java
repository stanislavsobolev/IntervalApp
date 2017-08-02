package com.sobolev;

import com.sobolev.model.Interval;
import com.sobolev.service.OptimizeIntervalsService;

import java.util.ArrayList;
import java.util.List;

public class TestFunctionality {
    public static void main(String[] args) {
        List<Interval> intervals = new ArrayList<Interval>();
        intervals.add(new Interval(1,2));
        intervals.add(new Interval(3,4));
        intervals.add(new Interval(5,6));
        intervals.add(new Interval(8,10));
        OptimizeIntervalsService op = new OptimizeIntervalsService();
        List<Interval> preparedIntervals = op.optimize(intervals);
        System.out.println(preparedIntervals);
    }
}
