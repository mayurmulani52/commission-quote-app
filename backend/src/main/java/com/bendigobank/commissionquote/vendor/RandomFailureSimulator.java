package com.bendigobank.commissionquote.vendor;

import com.bendigobank.commissionquote.config.VendorProperties;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class RandomFailureSimulator implements FailureSimulator {

    private final double failureRate;

    public RandomFailureSimulator(VendorProperties vendorProperties) {
        this.failureRate = vendorProperties.failureRate();
    }

    @Override
    public boolean shouldFail() {
        return ThreadLocalRandom.current().nextDouble() < failureRate;
    }
}
