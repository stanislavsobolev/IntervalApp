package com.sobolev.service;

import com.sobolev.model.Interval;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequestScope
public class OptimizeIntervalsService {

    private boolean optimized = false;
    private List<Interval> preparedIntervalsList;

    public List<Interval> optimize(List<Interval> intervals) {
        preparedIntervalsList = new ArrayList<>();
        if(intervals != null && !intervals.isEmpty()) {
            verifyData(intervals);
            optimizeFirstElement(intervals);
            Collections.sort(preparedIntervalsList);
        }
        return preparedIntervalsList;
    }

    public void optimizeFirstElement(List<Interval> intervals) {
        optimized = false;
        List<Interval> optimizedIntervalList = new ArrayList<>();
        for(int i = 1; i <= intervals.size() - 1; i++) {
            optimizedIntervalList.add(optimizeIfOverlap(intervals.get(0), intervals.get(i)));
        }
        if(!optimized) {
            preparedIntervalsList.add(intervals.get(0));
        }
        if(optimizedIntervalList.size() >= 1) {
            optimizeFirstElement(optimizedIntervalList);
        }
    }

    /*
    * This method used to optimise 2 Intervals if they overlap
    * According to method process, 4 points being produced:
    * [min  <  midMin  <=>  midMax  <  max]
    * If midMin > midMax then intervals overlap and will be united into single Interval
     */
    public Interval optimizeIfOverlap(Interval i1, Interval i2) {
        Integer min = null;
        Integer max = null;
        Integer midMin = null;
        Integer midMax = null;

        min = (i1.getStartI() <= i2.getStartI()) ? i1.getStartI() : i2.getStartI();
        midMin = (i1.getStartI() <= i2.getStartI()) ? i1.getEndI() : i2.getEndI();
        max = (i1.getEndI() >= i2.getEndI()) ? i1.getEndI() : i2.getEndI();
        midMax = (i1.getEndI() >= i2.getEndI()) ? i1.getStartI() : i2.getStartI();

        if(midMin >= midMax) {
            optimized = true;
            return  new Interval(min, max);
        }

        return i2;
    }

    public List<Interval> verifyData(List<Interval> intervals) {
            for (Interval i : intervals) {
                if (i.getStartI() > i.getEndI()) {
                    int startI = i.getEndI();
                    i.setEndI(i.getStartI());
                    i.setStartI(startI);
                }
            }
        return intervals;
    }
}
