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
public class RxTimer implements Parcelable {

    private final AtomicLong startTime = new AtomicLong(-1);
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicLong delta = new AtomicLong(0);
    private final Subject<Boolean, Boolean> runningSubject = BehaviorSubject.create(Boolean.FALSE);

    private static final long TICK_PERIOD = 100;

    public Observable<Long> start() {
        return Observable.timer(0, TICK_PERIOD, TimeUnit.MILLISECONDS)
                .takeWhile(index -> running.get())
                .map(index -> index * TICK_PERIOD)
                .map(tickTime -> tickTime + delta.get())
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
        running.set(false);
        runningSubject.onNext(Boolean.FALSE);
        startTime.set(-1);
        delta.set(0);
    }

    public Observable<Boolean> isRunning() {
        return runningSubject.distinctUntilChanged();
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

    public RxTimer() { }

    private RxTimer(Parcel in) {
        this.startTime.set(in.readLong());
        this.running.set(in.readByte() != 0);
        this.delta.set(in.readLong());
        this.runningSubject.onNext(this.running.get());
    }

    public static final Parcelable.Creator<RxTimer> CREATOR = new Parcelable.Creator<RxTimer>() {

        public RxTimer createFromParcel(Parcel source) {
            return new RxTimer(source);
        }

        public RxTimer[] newArray(int size) {
            return new RxTimer[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RxTimer)) {
            return false;
        }

        RxTimer that = (RxTimer) o;

        return running.equals(that.running) && startTime.equals(that.startTime);
    }

    @Override
    public int hashCode() {
        int result = startTime.hashCode();
        result = 31 * result + (running.hashCode());
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
