package com.jaggernod.pulluptraining.utils;

import android.support.annotation.NonNull;

/**
 * Created by Pawel Polanski on 23/10/14.
 */
public final class Utils {

    private static final double MILLISECONDS_IN_SECOND = 1000.;

    private Utils() { }

    public static long millisecondsToSeconds(@NonNull Long milliseconds) {
        return Math.round(milliseconds / MILLISECONDS_IN_SECOND);
    }

}
