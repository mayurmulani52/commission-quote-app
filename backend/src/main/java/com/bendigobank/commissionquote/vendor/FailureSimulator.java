package com.bendigobank.commissionquote.vendor;

// Interface (rather than inlining ThreadLocalRandom) so tests can force a deterministic outcome.
public interface FailureSimulator {
    boolean shouldFail();
}
