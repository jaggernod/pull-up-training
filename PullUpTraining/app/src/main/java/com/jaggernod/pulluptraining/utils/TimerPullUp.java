package com.jaggernod.pulluptraining.utils;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.Subject;

/**
 * Created by Pawel Polanski on 19/10/14.
 */
public class TimerPullUp implements Parcelable {

    private final AtomicLong startTime = new AtomicLong(-1);
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicLong delta = new AtomicLong(0);
    private final Subject<Boolean, Boolean> runningSubject = BehaviorSubject.create(Boolean.FALSE);

    public Observable<Long> start() {
        return Observable.timer(0, 100, TimeUnit.MILLISECONDS)
                .takeWhile(aLong -> running.get())
                .map(aLong -> aLong * 100)
                .map(aLong -> aLong + delta.get())
                .doOnCompleted(this::pause)
                .doOnError(throwable -> pause())
                .doOnSubscribe(this::startInternal);
    }

    private void startInternal() {
        if (startTime.get() != -1) {
            delta.getAndAdd(calculateElapsedTime(startTime.get()));
        }
        startTime.set(SystemClock.elapsedRealtime());
        running.set(true);
        runningSubject.onNext(Boolean.TRUE);
    }

    private static long calculateElapsedTime(final long startTime) {
        return SystemClock.elapsedRealtime() - startTime;
    }

    public void pause() {
        if (!running.get()) {
            return;
        }
        running.set(false);
        runningSubject.onNext(Boolean.FALSE);
        delta.getAndAdd(calculateElapsedTime(startTime.get()));
        startTime.set(-1);
    }

    public void stop() {
        if (!running.get()) {
            return;
        }
        running.set(false);
        runningSubject.onNext(Boolean.FALSE);
        startTime.set(-1);
        delta.set(0);
    }

    public Observable<Boolean> isRunning() {
        return runningSubject;
    }

    public long getTime() {
        long time = startTime.get();
        return time < 0 ? delta.get() : calculateElapsedTime(time) + delta.get();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(this.startTime.get());
        parcel.writeByte(this.running.get() ? (byte) 1 : (byte) 0);
        parcel.writeLong(this.delta.get());
    }

    public TimerPullUp() { }

    private TimerPullUp(Parcel in) {
        this.startTime.set(in.readLong());
        this.running.set(in.readByte() != 0);
        this.delta.set(in.readLong());
        this.runningSubject.onNext(this.running.get());
    }

    public static final Parcelable.Creator<TimerPullUp> CREATOR = new Parcelable.Creator<TimerPullUp>() {
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
