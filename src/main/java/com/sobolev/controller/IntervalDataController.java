package com.sobolev.controller;

import com.sobolev.service.IntervalDataService;
import com.sobolev.model.Interval;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.List;


@RestController
public class IntervalDataController {

    Logger log = LogManager.getLogger(IntervalDataController.class);

    WebApplicationContext applicationContext;

    @RequestMapping(value = "/append", method = RequestMethod.POST )
    public void subscribe(@RequestBody List<Interval> intervals) throws IOException {
        log.info("performing append request: " + intervals.size() + " intervals");
        ((IntervalDataService)applicationContext.getBean("intervalDataService")).insertNewIntegerIntervals(intervals);
    }

    @Autowired
    public void setApplicationContext(WebApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
