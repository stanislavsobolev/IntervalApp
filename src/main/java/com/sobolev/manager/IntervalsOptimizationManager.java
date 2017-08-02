package com.sobolev.manager;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.sql.Connection;
import java.sql.SQLException;

@Configuration
@EnableAsync
@EnableScheduling
public class IntervalsOptimizationManager {

    private final int SECOND = 1000;
    Connection con;

    @Scheduled(fixedDelay = 60 * 60 * SECOND)
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

}
