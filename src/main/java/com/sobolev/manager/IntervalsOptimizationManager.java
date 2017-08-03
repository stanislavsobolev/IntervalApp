package com.sobolev.manager;

import com.sobolev.service.IntervalDataService;
import com.sobolev.service.PostgreSQLConnectorService;
import com.sobolev.wrapper.IntervalDataServiceWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
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
    @Autowired
    IntervalDataService dataService;

    public IntervalsOptimizationManager() {

    }

    @Scheduled(fixedDelay = 6*SECOND)
    public void startScheduledOptimization() throws SQLException {
        System.out.println("WORKS");
    }

    @Bean
    public IntervalDataService intervalDataService() {
        return new IntervalDataService();
    }

    @Bean
    public IntervalDataServiceWrapper getConnectorService() {
        return new IntervalDataServiceWrapper(dataService);
    }

}
