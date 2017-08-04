package com.sobolev.manager;

import com.sobolev.enums.QueryType;
import com.sobolev.model.Query;
import com.sobolev.pool.QueriesPool;
import com.sobolev.service.IntervalDataService;
import com.sobolev.service.OptimizeIntervalsService;
import com.sobolev.service.PostgreSQLConnectorService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.context.WebApplicationContext;

import java.sql.Connection;

@Configuration
@ComponentScan("com.sobolev")
@EnableAsync
@EnableScheduling
public class QueriesPoolManager {

    private final int SECOND = 1000;

    Logger log = LogManager.getLogger(DatabaseConnectionManager.class);

    private QueriesPool queriesPool;

    /**
     * QueriesPoolManager scheduled to call handleQueriesPool() method automatically
     * once per minute
     */
    @Scheduled(fixedDelay = 60 * SECOND)
    public void handleQueriesPool() {
        handleQueries();
    }

    /**
     * If connection to db established again and queries queue is not empty,
     * QueriesPoolManager will call handleQueries() method.
     */
    public void handleQueries() {
        if(queriesPool.isConnectionOk() && !queriesPool.getQueries().isEmpty()) {
            log.info("Connection to database restored. Started handling pending queries");
            IntervalDataService dataService = new IntervalDataService(queriesPool, new OptimizeIntervalsService(), new PostgreSQLConnectorService());
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

    public QueriesPool getQueriesPool() {
        return queriesPool;
    }

    @Autowired
    public void setQueriesPool(QueriesPool queriesPool) {
        this.queriesPool = queriesPool;
    }
}
