package com.jaggernod.pulluptraining.activities;

import com.google.common.base.Preconditions;

import com.jaggernod.pulluptraining.helpers.StrictModeHelper;
import com.jaggernod.pulluptraining.utils.RetainedStateHelper;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.Subscription;

import static com.jaggernod.pulluptraining.utils.RetainedStateHelper.RetainedState;

/**
 * Created by Pawel Polanski on 15/10/14.
 */
public abstract class BaseActivity<T extends RetainedState> extends ActionBarActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    private RetainedStateHelper lifecycleHelper;
    private Map<String, Subscription> subscriptionMap = new ConcurrentHashMap<>();

    private T state;
    private final Class<T> stateClass;

    protected BaseActivity(Class<T> stateClass) {
        this.stateClass = stateClass;
    }

    protected BaseActivity() {
        this(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictModeHelper.registerActivity(this);
        lifecycleHelper = new RetainedStateHelper(this);
        if (stateClass != null) {
            state = getRetainedObject(stateClass);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        subscriptionMap.clear();
        lifecycleHelper.onDestroy();
    }

    protected void singleSubscription(@NonNull String key, @NonNull Subscription subscription) {
        if (subscriptionMap.containsKey(key)) {
            subscriptionMap.get(key).unsubscribe();
        }
        subscriptionMap.put(key, subscription);
    }

    protected <T> Observable<T> bind(@NonNull Observable<T> observable) {
        return lifecycleHelper.bindObservable(observable);
    }

    protected RetainedStateHelper getLifecycleHelper() {
        return lifecycleHelper;
    }

    protected <T extends RetainedState> T getRetainedObject(@NonNull Class<T> clazz) {
        return getLifecycleHelper().getRetainedState(clazz);
    }

    public T getState() {
        Preconditions.checkState(state != null, "State class not defined");

        return state;
    }

}
