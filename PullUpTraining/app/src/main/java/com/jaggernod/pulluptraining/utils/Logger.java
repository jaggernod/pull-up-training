/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Paweł Polański
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.jaggernod.pulluptraining.utils;

import com.jaggernod.pulluptraining.BuildConfig;

import android.os.SystemClock;
import android.util.Log;

/**
 * Created by ppol on 11/09/14.
 */
public class Logger {
    private final String tag;
    private final long startTime;

    public Logger(final String tag) {
        this(tag, false);
    }

    public Logger(final String tag, final boolean useTimer) {
        this.tag = tag;
        startTime = useTimer ? SystemClock.elapsedRealtime() : 0;
        Log.v(tag, "Create");
    }

    public void v(final String message) {
        v(tag, getMessage(message));
    }

    public void d(final String message) {
        d(tag, getMessage(message));
    }

    public void i(final String message) {
        i(tag, getMessage(message));
    }

    public void w(final String message) {
        w(tag, getMessage(message));
    }

    public void w(final String message, Throwable tr) {
        w(tag, getMessage(message), tr);
    }

    public void e(final String message) {
        e(tag, getMessage(message));
    }

    public void e(final String message, Throwable tr) {
        e(tag, getMessage(message), tr);
    }

    public static void v(final String tag, final String message) {
        Log.v(tag, getThreadInfo() + message);
    }

    public static void d(final String tag, final String message) {
        Log.d(tag, getThreadInfo() + message);
    }

    public static void i(final String tag, final String message) {
        Log.i(tag, getThreadInfo() + message);
    }

    public static void w(final String tag, final String message) {
        Log.w(tag, getThreadInfo() + message);
    }

    public static void w(final String tag, final String message, Throwable tr) {
        Log.w(tag, getThreadInfo() + message, tr);
    }

    public static void e(final String tag, final String message) {
        Log.e(tag, getThreadInfo() + message);
    }

    public static void e(final String tag, final String message, Throwable tr) {
        Log.e(tag, getThreadInfo() + message, tr);
    }

    private String getMessage(final String message) {
        return getTimestamp(message);
    }

    private static String getThreadInfo() {
        return (BuildConfig.DEBUG ? getThreadSignature() + ": " : "");
    }

    private String getTimestamp(final String message) {
        return startTime != 0
                ? SystemClock.elapsedRealtime() - startTime + "ms: " + message
                : message;
    }

    public static String getThreadSignature() {
        final Thread t = Thread.currentThread();
        return t.getName() + " P(" + t.getPriority() + ")";
    }

}
