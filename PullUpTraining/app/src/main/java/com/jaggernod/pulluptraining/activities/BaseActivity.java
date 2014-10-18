package com.jaggernod.pulluptraining.activities;

import com.jaggernod.pulluptraining.helpers.StrictModeHelper;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Pawel Polanski on 15/10/14.
 */
public class BaseActivity extends ActionBarActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    private CompositeSubscription subscriptions = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictModeHelper.registerActivityClass(getClass());
    }

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
