package com.sobolev.pool;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
public class QueriesPool {
    private boolean isConnectionOk;
    private int counter;

    public QueriesPool() {
        counter = 0;
    }

    public boolean isConnectionOk() {
        return isConnectionOk;
    }

    public void setConnectionOk(boolean connectionOk) {
        isConnectionOk = connectionOk;
    }
}
