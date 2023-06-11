package com.ivettepe.utils;

public class Time {
    // Кол-во наносекунд в секунде
    public static final long SECOND = 1000000000l;
    public static long get() {
        return System.nanoTime();
    }
}
