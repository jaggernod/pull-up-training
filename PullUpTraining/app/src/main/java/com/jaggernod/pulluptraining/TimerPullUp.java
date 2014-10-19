package com.jaggernod.pulluptraining;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import rx.Observable;

/**
 * Created by Pawel Polanski on 19/10/14.
 */
public class TimerPullUp implements Parcelable {

    private AtomicLong startTime = new AtomicLong(-1);
    private AtomicBoolean running = new AtomicBoolean(false);

    public Observable<Long> start() {
        if (startTime.get() < 0) {
            startTime.set(SystemClock.elapsedRealtime());
        }
        final long increment = (SystemClock.elapsedRealtime() - startTime.get()) / 100;
        return Observable.timer(0, 100, TimeUnit.MILLISECONDS)
                .map(aLong -> aLong + increment)
                .doOnCompleted(this::stop)
                .doOnError(throwable -> stop())
                .doOnSubscribe(() -> running.set(true));
    }

    public void stop() {
        running.set(false);
    }

    public boolean isRunning() {
        return running.get();
    }

    public long getTime() {
        long time = startTime.get();
        return time < 0 ? -1 : (SystemClock.elapsedRealtime() - time) / 100;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.startTime.get());
        dest.writeByte(this.running.get() ? (byte) 1 : (byte) 0);
    }

    public TimerPullUp() {
    }

    private TimerPullUp(Parcel in) {
        this.startTime.set(in.readLong());
        this.running.set(in.readByte() != 0);
    }

    public static final Parcelable.Creator<TimerPullUp> CREATOR
            = new Parcelable.Creator<TimerPullUp>() {
        public TimerPullUp createFromParcel(Parcel source) {
            return new TimerPullUp(source);
        }

        public TimerPullUp[] newArray(int size) {
            return new TimerPullUp[size];
        }
    };
}
