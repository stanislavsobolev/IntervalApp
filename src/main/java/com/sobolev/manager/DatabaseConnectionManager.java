package com.sobolev.manager;

import com.sobolev.service.PostgreSQLConnectorService;
import com.sobolev.wrapper.PostgreSQLServiceBeanWrapper;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

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

    @Autowired
    PostgreSQLConnectorService connectorService;
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
                System.out.println("OKAY");
                con = connectorService.establishConnectionToDB();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            if (con.isValid(SECOND)) {
                failCounter = 0;
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
    public PostgreSQLConnectorService postgreSQLConnectorService() {
        return new PostgreSQLConnectorService();
    }

    @Bean
    public PostgreSQLServiceBeanWrapper getConnectorService() {
        return new PostgreSQLServiceBeanWrapper(connectorService);
    }
}
