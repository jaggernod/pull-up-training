/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Paweł Polański
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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

    protected <U> Observable<U> bind(@NonNull Observable<U> observable) {
        return lifecycleHelper.bindObservable(observable);
    }

    protected RetainedStateHelper getLifecycleHelper() {
        return lifecycleHelper;
    }

    protected <U extends RetainedState> U getRetainedObject(@NonNull Class<U> clazz) {
        return getLifecycleHelper().getRetainedState(clazz);
    }

    public T getState() {
        Preconditions.checkState(state != null, "State class not defined");

        return state;
    }

}
