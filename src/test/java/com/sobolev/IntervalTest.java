package com.sobolev;

import com.sobolev.model.Interval;
import org.junit.Test;

import static org.junit.Assert.*;

public class IntervalTest {

    @Test
    public void intervalHashcodeTest() {
        Interval test = new Interval(3, 5);
        assertEquals(test.hashcode(), 35);
    }

    @Test
    public void intervalLargeHashcodeTest() {
        Interval test = new Interval(500000000, 700000000);
        assertEquals(test.hashcode(), -43309312);
    }

    @Test
    public void intervalToStringTest() {
        Interval test = new Interval(21, 33);
        assertEquals(test.toString(), "21_33");
    }
}
