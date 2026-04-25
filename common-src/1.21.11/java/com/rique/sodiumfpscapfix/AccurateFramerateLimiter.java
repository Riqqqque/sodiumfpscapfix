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
        if (framerateLimit <= 0) {
            return;
        }

        if (framerateLimit != lastFramerateLimit) {
            averageOvershootNs = 0L;
            lastFramerateLimit = framerateLimit;
            lastFrameTimeNs = System.nanoTime();
        }

        long targetTimePerFrameNs = (ONE_SECOND_NS + framerateLimit - 1L) / framerateLimit;
        long spinSafetyBufferNs = spinSafetyBufferNs(framerateLimit);
        long targetTimeNs = lastFrameTimeNs + targetTimePerFrameNs;
        long remainingTimeNs;

        while ((remainingTimeNs = targetTimeNs - System.nanoTime()) > 0L) {
            if (remainingTimeNs > averageOvershootNs + spinSafetyBufferNs) {
                long sleepStartTimeNs = System.nanoTime();
                long expectedSleepTimeNs = remainingTimeNs - averageOvershootNs - spinSafetyBufferNs;

                if (!Thread.interrupted()) {
                    LockSupport.parkNanos(expectedSleepTimeNs);
                    updateAverageOvershoot(System.nanoTime() - sleepStartTimeNs - expectedSleepTimeNs);
                }
            } else {
                Thread.onSpinWait();
            }
        }

        lastFrameTimeNs = System.nanoTime();
    }

    private static long spinSafetyBufferNs(int framerateLimit) {
        return framerateLimit >= HIGH_ACCURACY_FPS_THRESHOLD ? HIGH_ACCURACY_SPIN_SAFETY_BUFFER_NS : NORMAL_SPIN_SAFETY_BUFFER_NS;
    }

    private static void updateAverageOvershoot(long currentOvershootNs) {
        if (currentOvershootNs > 0L && currentOvershootNs < MAX_CURRENT_OVERSHOOT_NS) {
            averageOvershootNs = (long) ((0.1 * currentOvershootNs) + (0.9 * averageOvershootNs));
            averageOvershootNs = Math.min(averageOvershootNs, MAX_AVERAGE_OVERSHOOT_NS);
        }
    }
}
