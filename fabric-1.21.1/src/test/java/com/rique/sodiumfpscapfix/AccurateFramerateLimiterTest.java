package com.rique.sodiumfpscapfix;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AccurateFramerateLimiterTest {
    private static final long MILLIS_TO_NANOS = 1_000_000L;

    @Test
    void unlimitedLimitDoesNotSleep() {
        long start = System.nanoTime();
        AccurateFramerateLimiter.limitDisplayFPS(0);
        AccurateFramerateLimiter.limitDisplayFPS(0);
        assertTrue(System.nanoTime() - start < 50 * MILLIS_TO_NANOS);
    }

    @Test
    void cappedFrameWaitsForFullFrameTime() {
        AccurateFramerateLimiter.limitDisplayFPS(10);

        long start = System.nanoTime();
        AccurateFramerateLimiter.limitDisplayFPS(10);
        long elapsed = System.nanoTime() - start;

        assertTrue(elapsed >= 95 * MILLIS_TO_NANOS, "limiter returned early: " + elapsed);
    }

    @Test
    void interruptedThreadDoesNotSleep() {
        AccurateFramerateLimiter.limitDisplayFPS(10);
        Thread.currentThread().interrupt();

        try {
            long start = System.nanoTime();
            AccurateFramerateLimiter.limitDisplayFPS(10);
            assertTrue(System.nanoTime() - start < 50 * MILLIS_TO_NANOS);
        } finally {
            Thread.interrupted();
        }
    }

    @Test
    void limitChangeResetsFrameTiming() {
        AccurateFramerateLimiter.limitDisplayFPS(1_000_000);

        long start = System.nanoTime();
        AccurateFramerateLimiter.limitDisplayFPS(10);
        long elapsed = System.nanoTime() - start;

        assertTrue(elapsed >= 95 * MILLIS_TO_NANOS, "new limit ignored: " + elapsed);
    }
}
