package com.mccritz.kpure.utils;

public class Lag implements Runnable {

    public static int TickCount;
    public static long[] Ticks;
    public static long LastTick;

    static {
        Lag.TickCount = 0;
        Lag.Ticks = new long[600];
        Lag.LastTick = 0L;
    }

    public static double getTPS() {
        return getTPS(100);
    }

    public static double getTPS(final int n) {
        if (Lag.TickCount < n) {
            return 20.0;
        }
        return n / ((System.currentTimeMillis() - Lag.Ticks[(Lag.TickCount - 1 - n) % Lag.Ticks.length]) / 1000.0);
    }

    public static long getElapsed(final int n) {
        if (Lag.TickCount - n >= Lag.Ticks.length) {
            return (long) ((Lag.TickCount - n) * getTPS());
        }
        return System.currentTimeMillis() - Lag.Ticks[n % Lag.Ticks.length];
    }

    @Override
    public void run() {
        Lag.Ticks[Lag.TickCount % Lag.Ticks.length] = System.currentTimeMillis();
        ++Lag.TickCount;
    }
}
