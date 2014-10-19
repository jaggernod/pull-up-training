package com.jaggernod.pulluptraining.utils;

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
        return Observable.timer(0, 100, TimeUnit.MILLISECONDS)
                .takeWhile(aLong -> running.get())
                .map(aLong -> aLong + increment())
                .doOnCompleted(this::pause)
                .doOnError(throwable -> pause())
                .doOnSubscribe(this::startInternal);
    }

    private void startInternal() {
        if (startTime.get() < 0) {
            startTime.set(SystemClock.elapsedRealtime());
        }
        running.set(true);
    }

    private long increment() {
        return (SystemClock.elapsedRealtime() - startTime.get()) / 100;
    }

    public void pause() {
        running.set(false);
    }

    public void stop() {
        running.set(false);
        startTime.set(-1);
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
        this.startTime.set(in.readLong() + 1);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TimerPullUp)) {
            return false;
        }

        TimerPullUp that = (TimerPullUp) o;

        if (running != null ? !running.equals(that.running) : that.running != null) {
            return false;
        }
        if (startTime != null ? !startTime.equals(that.startTime) : that.startTime != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = startTime != null ? startTime.hashCode() : 0;
        result = 31 * result + (running != null ? running.hashCode() : 0);
        return result;
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TimerPullUp{");
        sb.append("startTime=").append(startTime);
        sb.append(", running=").append(running);
        sb.append('}');
        return sb.toString();
    }
}
