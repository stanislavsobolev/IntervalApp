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
    public List<Interval> appendIntervals(@RequestBody List<Interval> intervals) throws IOException {
        log.info("Performing append request: " + intervals.size() + " intervals");
        return ((IntervalDataService)applicationContext.getBean("intervalDataService")).appendIntervals(intervals);
    }

    @RequestMapping(value = "/select", method = RequestMethod.POST )
    public List<Interval> selectIntervals(@RequestBody Interval interval) throws IOException {
        log.info("Performing select request: searching for [" + interval.getStartI() + ", " + interval.getEndI() + "] " + " limits");
        return ((IntervalDataService)applicationContext.getBean("intervalDataService")).selectIntervals(interval);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE )
    public void deleteIntervals(@RequestBody List<Interval> intervals) throws IOException {
        log.info("Performing delete request: removing " + intervals.size() + " intervals from database");
        ((IntervalDataService)applicationContext.getBean("intervalDataService")).deleteIntervals(intervals);
    }

    @Autowired
    public void setApplicationContext(WebApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
