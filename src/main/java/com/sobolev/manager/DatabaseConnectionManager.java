package com.sobolev.manager;

import com.sobolev.model.Interval;
import com.sobolev.pool.QueriesPool;
import com.sobolev.service.IntervalDataService;
import com.sobolev.service.OptimizeIntervalsService;
import com.sobolev.service.PostgreSQLConnectorService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.context.WebApplicationContext;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @class DatabaseConnectionManager scheduled to check if connection to database exist
 */
@Configuration
@ComponentScan("com.sobolev")
@EnableAsync
@EnableScheduling
public class DatabaseConnectionManager {

    private Integer failCounter = 0;
    private final int SECOND = 1000;

    Logger log = LogManager.getLogger(DatabaseConnectionManager.class);

    WebApplicationContext applicationContext;
    PostgreSQLConnectorService connectorService;
    private QueriesPool queriesPool;
    Connection con;

    public DatabaseConnectionManager() {
        connectorService = new PostgreSQLConnectorService();
    }

    /**
     * This method will be executed automatically each 30 seconds to ensure connection
     * to database exist
     * <p>
     * If there is no connection, failCounter will be incremented, and on failCounter >= 20
     * method will throw Error and stop application
     */
    @Scheduled(fixedDelay = 5 * SECOND)
    public void doConnectionCheck() {
        try {
            if (con == null || con.isClosed()) {
                con = connectorService.establishConnectionToDB();
            }
        }
        catch (Exception e) {
            log.error(e);
            queriesPool.setConnectionOk(false);
            failCounter++;
            log.info("Cannot establish connection with database. Number of attempts: " + failCounter);
        }
        finally {
            if (failCounter >= 120) {
                log.error("Database connection timeout reached. Closing application");
                SpringApplication.exit(applicationContext);
            }
        }
        try {
            if (con != null && !con.isClosed() && con.isValid(SECOND)) {
                queriesPool.setConnectionOk(true);
                failCounter = 0;
            }
        } catch (SQLException e) {
            log.error(e);
        }
    }

    @Autowired
    public void setQueriesPool(QueriesPool queriesPool) {
        this.queriesPool = queriesPool;
    }

    @Autowired
    public void setApplicationContext(WebApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
