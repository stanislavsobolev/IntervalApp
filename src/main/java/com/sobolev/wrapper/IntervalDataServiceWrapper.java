package com.sobolev.wrapper;

import com.sobolev.service.IntervalDataService;

public class IntervalDataServiceWrapper {

    IntervalDataService dataService;

    public IntervalDataServiceWrapper(IntervalDataService dataService) {
        this.dataService = dataService;
    }
}
