package com.sobolev.manager;

import com.sobolev.enums.QueryType;
import com.sobolev.model.Query;
import com.sobolev.pool.QueriesPool;
import com.sobolev.service.IntervalDataService;
import com.sobolev.service.PostgreSQLConnectorService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.sql.Connection;

@Configuration
@ComponentScan("com.sobolev")
@EnableAsync
@EnableScheduling
public class QueriesPoolHandler {

    private final int SECOND = 1000;

    Logger log = LogManager.getLogger(DatabaseConnectionManager.class);

    IntervalDataService dataService;
    PostgreSQLConnectorService connectorService;
    private QueriesPool queriesPool;
    Connection con;

    @Scheduled(fixedDelay = 60 * SECOND)
    public void handleQueriesPool() {
        handleQueries();
    }

    public void handleQueries() {
        if(queriesPool.isConnectionOk() && !queriesPool.getQueries().isEmpty()) {
            Query query = queriesPool.getQueries().poll();
            if(query.getQueryType() == QueryType.APPEND) {
                dataService.appendIntervals(query.getIntervals());
            }
            if(query.getQueryType() == QueryType.DELETE) {
                dataService.deleteIntervals(query.getIntervals());
            }
            handleQueries();
        }
    }

    public PostgreSQLConnectorService getConnectorService() {
        return connectorService;
    }

    @Autowired
    public void setConnectorService(PostgreSQLConnectorService connectorService) {
        this.connectorService = connectorService;
    }

    public QueriesPool getQueriesPool() {
        return queriesPool;
    }

    @Autowired
    public void setQueriesPool(QueriesPool queriesPool) {
        this.queriesPool = queriesPool;
    }

    public IntervalDataService getDataService() {
        return dataService;
    }

    @Autowired
    public void setDataService(IntervalDataService dataService) {
        this.dataService = dataService;
    }
}
