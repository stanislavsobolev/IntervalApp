package com.sobolev.model;

import com.sobolev.enums.QueryType;

import java.util.ArrayList;
import java.util.List;

public class Query {
    private List<Interval> intervals;
    private QueryType queryType;

    public Query(List<Interval> intervals, QueryType queryType) {
        if(intervals != null) {
            this.intervals = new ArrayList<>();
            for(Interval i : intervals) {
                this.intervals.add(new Interval(i));
            }
        }
        this.queryType = queryType;
    }

    public List<Interval> getIntervals() {
        return intervals;
    }

    public void setIntervals(List<Interval> intervals) {
        this.intervals = intervals;
    }

    public QueryType getQueryType() {
        return queryType;
    }

    public void setQueryType(QueryType queryType) {
        this.queryType = queryType;
    }
}
