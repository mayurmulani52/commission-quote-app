package com.bendigobank.commissionquote.vendor;

/**
 * Extracted as an interface (rather than inlining ThreadLocalRandom in the
 * controller) purely so tests can force deterministic success/failure
 * without relying on randomness or a 0% configured failure rate.
 */
public interface FailureSimulator {
    boolean shouldFail();
}
