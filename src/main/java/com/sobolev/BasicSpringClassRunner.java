package com.sobolev;

import com.sobolev.model.Interval;
import com.sobolev.service.IntervalDataService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BasicSpringClassRunner {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("basic-context");
        IntervalDataService projectService = (IntervalDataService) context.getBean("basicProjectService");
    }
}
