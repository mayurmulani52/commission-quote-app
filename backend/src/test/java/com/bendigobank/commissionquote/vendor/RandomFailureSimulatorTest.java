package com.bendigobank.commissionquote.vendor;

import com.bendigobank.commissionquote.config.VendorProperties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RandomFailureSimulatorTest {

    @Test
    void neverFailsWhenFailureRateIsZero() {
        RandomFailureSimulator simulator = new RandomFailureSimulator(
                new VendorProperties("http://localhost:8080", "key", 0.0, 2000, 3000));

        for (int i = 0; i < 1000; i++) {
            assertFalse(simulator.shouldFail());
        }
    }

    @Test
    void alwaysFailsWhenFailureRateIsOne() {
        RandomFailureSimulator simulator = new RandomFailureSimulator(
                new VendorProperties("http://localhost:8080", "key", 1.0, 2000, 3000));

        for (int i = 0; i < 1000; i++) {
            assertTrue(simulator.shouldFail());
        }
    }
}
