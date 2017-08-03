package com.sobolev.pool;

import com.sobolev.model.Interval;
import com.sobolev.model.Query;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

@Service
@Scope("singleton")
public class QueriesPool {
    private boolean isConnectionOk;
    private List<Interval> appendIntervals;
    private List<Interval> deleteIntervals;

    private Queue<Query> queries;

    public QueriesPool() {
        appendIntervals = new CopyOnWriteArrayList<>();
        deleteIntervals = new CopyOnWriteArrayList<>();
        queries = new ConcurrentLinkedQueue<>();
    }

    public boolean isConnectionOk() {
        return isConnectionOk;
    }

    public void setConnectionOk(boolean connectionOk) {
        isConnectionOk = connectionOk;
    }

    public synchronized void setAppendIntervals(List<Interval> intervals) {
        appendIntervals.addAll(intervals);
    }

    public List<Interval> getAppendIntervals() {
        return appendIntervals;
    }

    public synchronized void setDeleteIntervals(List<Interval> intervals) {
        deleteIntervals.addAll(intervals);

    }

    public List<Interval> getDeleteIntervals() {
        return deleteIntervals;
    }

    public Queue<Query> getQueries() {
        return queries;
    }

    public void addQuery(Query query) {
        this.queries.add(query);
    }
}
