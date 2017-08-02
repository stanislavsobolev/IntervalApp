package com.sobolev.controller;

import com.sobolev.service.IntervalDataService;
import com.sobolev.model.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@RestController
public class IntervalDataController {

    WebApplicationContext applicationContext;

    @RequestMapping(value = "/subscribe", method = RequestMethod.POST )
    public void subscribe(@RequestBody List<Interval> intervals) throws IOException {
        ((IntervalDataService)applicationContext.getBean("intervalDataService")).insertNewIntegerIntervals(intervals);
    }

    @Autowired
    public void setApplicationContext(WebApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
