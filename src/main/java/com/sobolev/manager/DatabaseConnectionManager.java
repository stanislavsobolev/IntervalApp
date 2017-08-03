package com.sobolev.manager;

import com.sobolev.model.Interval;
import com.sobolev.pool.QueriesPool;
import com.sobolev.service.IntervalDataService;
import com.sobolev.service.OptimizeIntervalsService;
import com.sobolev.service.PostgreSQLConnectorService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *@class DatabaseConnectionManager scheduled to check if connection to database exist
 */
@Configuration
@ComponentScan("com.sobolev")
@EnableAsync
@EnableScheduling
public class DatabaseConnectionManager {

    @Autowired
    Environment env;

    private QueriesPool queriesPool;

    Logger log = LogManager.getLogger(DatabaseConnectionManager.class);

    private final int SECOND = 1000;
    private Integer failCounter = 0;

    PostgreSQLConnectorService connectorService;
    Connection con;

    public DatabaseConnectionManager() {
        connectorService = new PostgreSQLConnectorService();
    }

    /**
     * This method will be executed automatically each 60 seconds to ensure connection
     * to database exist
     * <p>
     * If there is no connection, failCounter will be incremented, and on failCounter >= 10
     * method will throw Error and stop application
     */
    @Scheduled(fixedDelay = 15 * SECOND)
    public void doConnectionCheck() {
        if (con == null) {
            try {
                con = connectorService.establishConnectionToDB();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            if (con.isValid(SECOND)) {
                queriesPool.setConnectionOk(true);
                handleQueriesPool();
                failCounter = 0;
            }
            if (!con.isValid(SECOND)) {
                queriesPool.setConnectionOk(false);
                failCounter++;
                log.info("Cannot establish connection with database. Number of attempts: " + failCounter);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (failCounter >= 40) {
            log.error("Database connection timeout reached. Closing application");
            throw new Error();
        }
    }

    private void handleQueriesPool() {
        List<Interval> handledAppendIntervals;
        List<Interval> handledDeleteIntervals;
        synchronized (queriesPool) {
            handledAppendIntervals = new ArrayList<>(queriesPool.getAppendIntervals());
            queriesPool.getAppendIntervals().clear();
            handledDeleteIntervals = new ArrayList<>(queriesPool.getDeleteIntervals());
            queriesPool.getDeleteIntervals().clear();
        }
        new IntervalDataService(new OptimizeIntervalsService(), new PostgreSQLConnectorService()).insertNewIntegerIntervals(handledAppendIntervals);
        new IntervalDataService(new OptimizeIntervalsService(), new PostgreSQLConnectorService()).deleteIntervals(handledDeleteIntervals);
    }

    @Autowired
    public void setQueriesPool(QueriesPool queriesPool) {
        this.queriesPool = queriesPool;
    }
}
