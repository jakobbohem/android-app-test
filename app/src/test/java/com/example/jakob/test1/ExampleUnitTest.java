package com.example.jakob.test1;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test_split() throws Exception {
        String teststring = "jakob #0702";
        String number = teststring.split("#")[1];

        assertEquals(number, "0702");
    }
}