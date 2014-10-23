package com.jaggernod.pulluptraining.utils;

import android.support.annotation.NonNull;

/**
 * Created by Pawel Polanski on 23/10/14.
 */
public class Utils {

    private Utils() {}

    public static long millisecondsToSeconds(@NonNull Long milliseconds) {
        return Math.round(milliseconds / 1000.);
    }

}
