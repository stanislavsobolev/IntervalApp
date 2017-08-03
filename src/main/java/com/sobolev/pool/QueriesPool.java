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
    private Queue<Query> queries;

    public QueriesPool() {
        queries = new ConcurrentLinkedQueue<>();
    }

    public boolean isConnectionOk() {
        return isConnectionOk;
    }

    public void setConnectionOk(boolean connectionOk) {
        isConnectionOk = connectionOk;
    }

    public Queue<Query> getQueries() {
        return queries;
    }

    public void addQuery(Query query) {
        this.queries.add(query);
    }
}
