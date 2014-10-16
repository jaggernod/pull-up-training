package com.jaggernod.pulluptraining.activities;

import android.app.Activity;
import android.support.annotation.NonNull;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Pawel Polanski on 15/10/14.
 */
public class BaseActivity extends Activity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    private CompositeSubscription subscriptions = new CompositeSubscription();

    protected void registerSubscription(@NonNull Subscription subscription) {
        subscriptions.add(subscription);
    }

    protected void clearSubscriptions() {
        subscriptions.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearSubscriptions();
    }

}
