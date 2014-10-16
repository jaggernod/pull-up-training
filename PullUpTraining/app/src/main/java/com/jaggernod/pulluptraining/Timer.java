package com.jaggernod.pulluptraining;

import android.os.SystemClock;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Pawel Polanski on 10/16/14.
 */
public class Timer {

    private volatile long startTime = -1;

    public void start() {
        startTime = SystemClock.elapsedRealtime();
    }

    public void stop() {
        startTime = -1;
    }

    public Observable<Long> asObservable() {
        return Observable.create(new Observable.OnSubscribe<Long>() {
            @Override
            public void call(Subscriber<? super Long> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    while(startTime >= 0) {
                        subscriber.onNext(SystemClock.elapsedRealtime() - startTime);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            subscriber.onError(e);
                            return;
                        }
                    }
                    subscriber.onCompleted();

                }
            }
        });
    }

}
