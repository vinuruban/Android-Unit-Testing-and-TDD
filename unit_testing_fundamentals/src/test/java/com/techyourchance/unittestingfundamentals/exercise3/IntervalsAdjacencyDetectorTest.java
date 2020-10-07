package com.techyourchance.unittestingfundamentals.exercise3;

import com.techyourchance.unittestingfundamentals.example3.Interval;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class IntervalsAdjacencyDetectorTest {

    IntervalsAdjacencyDetector SUT;

    @Before
    public void setUp() throws Exception {
        SUT = new IntervalsAdjacencyDetector();
    }

    @Test
    public void isAdjacent_noOverlapInterval1BeforeInterval2_falseReturned() {
        Interval interval1 = new Interval(-1, 5);
        Interval interval2 = new Interval(6, 12);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }

    @Test
    public void isAdjacent_noOverlapInterval1AfterInterval2_falseReturned() {
        Interval interval1 = new Interval(13, 21);
        Interval interval2 = new Interval(6, 12);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }

    @Test
    public void isAdjacent_overlapInterval1BeforeInterval2_falseReturned() {
        Interval interval1 = new Interval(1, 21);
        Interval interval2 = new Interval(20, 31);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }

    @Test
    public void isAdjacent_overlapInterval1AfterInterval2_falseReturned() {
        Interval interval1 = new Interval(11, 18);
        Interval interval2 = new Interval(6, 12);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }

    @Test
    public void isAdjacent_adjacentInterval1BeforeInterval2_trueReturned() {
        Interval interval1 = new Interval(-3, 6);
        Interval interval2 = new Interval(6, 12);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(true));
    }

    @Test
    public void isAdjacent_adjacentInterval1AfterInterval2_trueReturned() {
        Interval interval1 = new Interval(15, 22);
        Interval interval2 = new Interval(9, 15);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(true));
    }

    @Test
    public void isAdjacent_adjacentInterval1EqualsInterval2_trueReturned() {
        Interval interval1 = new Interval(-15, 22);
        Interval interval2 = new Interval(-15, 22);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(true));
    }

}