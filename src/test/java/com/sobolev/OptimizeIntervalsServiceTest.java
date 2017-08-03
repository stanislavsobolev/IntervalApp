package com.sobolev;

import com.sobolev.model.Interval;
import com.sobolev.service.OptimizeIntervalsService;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

public class OptimizeIntervalsServiceTest {

    OptimizeIntervalsService test;

    @Before
    public void prepareOptimizeService() {
        test = new OptimizeIntervalsService();
    }

    @Test
    public void optimizeCorrectIntervalsListTest() {
        List<Interval> correctData = new ArrayList<>();
        correctData.add(new Interval(1,2));
        correctData.add(new Interval(3,4));
        correctData.add(new Interval(2,3));
        correctData = test.optimize(correctData);
        assertEquals(correctData.size(), 1);
        assertEquals(correctData.get(0), new Interval(1,4));
    }


    @Test
    public void optimizeIncorrectIntervalsListTest() {
        List<Interval> incorrectData = new ArrayList<>();
        incorrectData.add(new Interval(1,2));
        incorrectData.add(new Interval(4,3));
        incorrectData = test.optimize(incorrectData);
        assertEquals(incorrectData.size(), 2);
        assertEquals(incorrectData.get(1), new Interval(3,4));
    }

    @Test
    public void optimizeEmptyIntervalsListTest() {
        List<Interval> emptyData = new ArrayList<>();
        emptyData = test.optimize(emptyData);
        assertEquals(emptyData.size(), 0);
    }
}
