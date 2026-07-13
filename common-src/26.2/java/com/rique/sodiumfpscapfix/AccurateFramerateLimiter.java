package com.rique.sodiumfpscapfix;

import java.util.concurrent.locks.LockSupport;

public final class AccurateFramerateLimiter {
    private static final long MAX_CURRENT_OVERSHOOT_NS = 25_000_000L;
    private static final long MAX_AVERAGE_OVERSHOOT_NS = 2_000_000L;
    private static final long ONE_SECOND_NS = 1_000_000_000L;
    private static final long NORMAL_SPIN_SAFETY_BUFFER_NS = 500_000L;
    private static final long HIGH_ACCURACY_SPIN_SAFETY_BUFFER_NS = 1_500_000L;
    private static final int HIGH_ACCURACY_FPS_THRESHOLD = 260;

    private static long lastFrameTimeNs = System.nanoTime();
    private static long averageOvershootNs;
    private static int lastFramerateLimit;

    private AccurateFramerateLimiter() {
    }

    public static void limitDisplayFPS(int framerateLimit) {
        long now = System.nanoTime();

        if (framerateLimit <= 0 || Thread.currentThread().isInterrupted()) {
            resetTiming(framerateLimit, now);
            return;
        }

        if (framerateLimit != lastFramerateLimit) {
            resetTiming(framerateLimit, now);
        }

        long targetTimePerFrameNs = (ONE_SECOND_NS + framerateLimit - 1L) / framerateLimit;
        long spinSafetyBufferNs = spinSafetyBufferNs(framerateLimit);
        long targetTimeNs = lastFrameTimeNs + targetTimePerFrameNs;
        long remainingTimeNs;

        while ((remainingTimeNs = targetTimeNs - System.nanoTime()) > 0L) {
            if (Thread.currentThread().isInterrupted()) {
                resetTiming(framerateLimit, System.nanoTime());
                return;
            }

            if (remainingTimeNs > averageOvershootNs + spinSafetyBufferNs) {
                long sleepStartTimeNs = System.nanoTime();
                long expectedSleepTimeNs = remainingTimeNs - averageOvershootNs - spinSafetyBufferNs;

                LockSupport.parkNanos(expectedSleepTimeNs);
                updateAverageOvershoot(System.nanoTime() - sleepStartTimeNs - expectedSleepTimeNs);
            } else {
                Thread.onSpinWait();
            }
        }

        lastFrameTimeNs = System.nanoTime();
    }

    private static void resetTiming(int framerateLimit, long now) {
        averageOvershootNs = 0L;
        lastFramerateLimit = framerateLimit;
        lastFrameTimeNs = now;
    }

    private static long spinSafetyBufferNs(int framerateLimit) {
        return framerateLimit >= HIGH_ACCURACY_FPS_THRESHOLD ? HIGH_ACCURACY_SPIN_SAFETY_BUFFER_NS : NORMAL_SPIN_SAFETY_BUFFER_NS;
    }

    private static void updateAverageOvershoot(long currentOvershootNs) {
        if (currentOvershootNs > 0L && currentOvershootNs < MAX_CURRENT_OVERSHOOT_NS) {
            averageOvershootNs = (currentOvershootNs + (9L * averageOvershootNs)) / 10L;
            averageOvershootNs = Math.min(averageOvershootNs, MAX_AVERAGE_OVERSHOOT_NS);
        }
    }
}
