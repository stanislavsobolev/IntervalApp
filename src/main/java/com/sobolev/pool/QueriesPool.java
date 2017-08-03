package com.sobolev.pool;

import com.sobolev.model.Interval;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Scope("singleton")
public class QueriesPool {
    private boolean isConnectionOk;
    private List<Interval> appendIntervals;
    private List<Interval> deleteIntervals;

    public QueriesPool() {
        appendIntervals = new CopyOnWriteArrayList<>();
        deleteIntervals = new CopyOnWriteArrayList<>();
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
}
