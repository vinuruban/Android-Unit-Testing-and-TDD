package com.techyourchance.unittestingfundamentals.exercise2;

import com.techyourchance.unittestingfundamentals.exercise1.NegativeNumberValidator;

import org.junit.Assert;
import org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StringDuplicatorTest {

    StringDuplicator SUT;

    @Before
    public void setUp() throws Exception {
        SUT = new StringDuplicator();
    }

    @Test
    public void duplicate_emptyString_emptyStringReturned() {
        String result = SUT.duplicate("");
        assertThat(result, is(""));
    }

    @Test
    public void duplicate_number_duplicateNumberReturned() {
        String result = SUT.duplicate("0");
        assertThat(result, is("00"));
    }

    @Test
    public void duplicate_string_duplicateStringReturned() {
        String result = SUT.duplicate("Hello");
        assertThat(result, is("HelloHello"));
    }

}