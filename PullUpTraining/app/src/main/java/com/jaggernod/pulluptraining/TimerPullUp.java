package com.jaggernod.pulluptraining;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;

import java.util.concurrent.TimeUnit;

import rx.Observable;

/**
 * Created by Pawel Polanski on 19/10/14.
 */
public class TimerPullUp implements Parcelable {

    private long startTime = -1;
    private boolean running = false;

    public Observable<Long> start() {
        if (startTime < 0) {
            startTime = SystemClock.elapsedRealtime();
        }
        final long increment = (SystemClock.elapsedRealtime() - startTime) / 100;
        return Observable.timer(0, 100, TimeUnit.MILLISECONDS)
                .map(aLong -> aLong + increment)
                .doOnCompleted(this::stop)
                .doOnError(throwable -> stop())
                .doOnSubscribe(() -> running = true);
    }

    public void stop() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    public long getTime() {
        return startTime < 0 ? -1 : (SystemClock.elapsedRealtime() - startTime) / 100;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.startTime);
        dest.writeByte(running ? (byte) 1 : (byte) 0);
    }

    public TimerPullUp() {
    }

    private TimerPullUp(Parcel in) {
        this.startTime = in.readLong();
        this.running = in.readByte() != 0;
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
