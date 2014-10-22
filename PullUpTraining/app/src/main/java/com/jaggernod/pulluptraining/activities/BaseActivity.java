package com.jaggernod.pulluptraining.activities;

import com.jaggernod.pulluptraining.helpers.StrictModeHelper;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Pawel Polanski on 15/10/14.
 */
public class BaseActivity extends ActionBarActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    private CompositeSubscription subscriptions = new CompositeSubscription();
    private Map<String, Subscription> subscriptionMap = new ConcurrentHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictModeHelper.registerActivity(this);
    }

    protected void registerSubscription(@NonNull Subscription subscription) {
        subscriptions.add(subscription);
    }

    protected void registerSubscription(@NonNull String key, @NonNull Subscription subscription) {
        if (subscriptionMap.containsKey(key)) {
            subscriptionMap.get(key).unsubscribe();
        }
        subscriptionMap.put(key, subscription);
    }

    protected void clearSubscription(@NonNull String key) {
        if (subscriptionMap.containsKey(key)) {
            subscriptionMap.get(key).unsubscribe();
            subscriptionMap.remove(key);
        }
    }

    protected void clearSubscriptions() {
        subscriptions.clear();
    }

    @Override
    protected void onDestroy() {
        for(Subscription subscription: subscriptionMap.values()) {
            subscription.unsubscribe();
        }
        subscriptionMap.clear();
        clearSubscriptions();
        super.onDestroy();
    }

}
