package com.sobolev.manager;

import com.sobolev.service.IntervalDataService;
import com.sobolev.service.OptimizeIntervalsService;
import com.sobolev.service.PostgreSQLConnectorService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.sql.SQLException;

/**
 * Class scheduled to optimize all data in test_interval table
 * Default value - once per 1 hour
 */
@Configuration
@EnableAsync
@EnableScheduling
public class IntervalsOptimizationManager {

    private final int SECOND = 1000;


    public IntervalsOptimizationManager() {
    }

    @Scheduled(fixedDelay = 60 * 60 * SECOND)
    public void startScheduledOptimization() throws SQLException {
        new IntervalDataService(new OptimizeIntervalsService(), new PostgreSQLConnectorService()).optimizeAllData();
    }

}
