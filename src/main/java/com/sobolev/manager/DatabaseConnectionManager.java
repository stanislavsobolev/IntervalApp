package com.sobolev.manager;

import com.sobolev.service.PostgreSQLConnectorService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *@class DatabaseConnectionManager scheduled to check if connection to database exist
 */
@Configuration
@EnableAsync
@EnableScheduling
public class DatabaseConnectionManager {

    Logger log = LogManager.getLogger(DatabaseConnectionManager.class);

    private final int SECOND = 1000;
    private Integer failCounter = 0;

    Connection con;

    public DatabaseConnectionManager() {
    }

    /**
     * This method will be executed automatically each 60 seconds to ensure connection
     * to database exist
     * <p>
     * If there is no connection, failCounter will be incremented, and on failCounter >= 10
     * method will throw Error and stop application
     */
    @Scheduled(fixedDelay = 60 * SECOND)
    public void doConnectionCheck() {
        if (con == null) {
            try {
                con = this.getConnectorService().establishConnectionToDB();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            if (con.isValid(SECOND)) {
                failCounter = 0;
                log.info("Connection to database exist");
            }
            if (!con.isValid(SECOND)) {
                failCounter++;
                log.info("Cannot establish connection with database. Number of attempts: " + failCounter);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (failCounter >= 10) {
            log.error("Database connection timeout reached. Closing application");
            throw new Error();
        }
    }

    @Bean
    public PostgreSQLConnectorService getConnectorService() {
        return new PostgreSQLConnectorService();
    }
}
