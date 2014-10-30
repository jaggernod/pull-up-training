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

import com.jaggernod.pulluptraining.R;
import com.jaggernod.pulluptraining.utils.RetainedStateHelper;
import com.jaggernod.pulluptraining.utils.RxTicker;
import com.jaggernod.pulluptraining.utils.Utils;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Pawel Polanski on 14/10/14.
 */
public class MainActivity extends BaseActivity<MainActivity.TimerState> {

    private static final String TAG = MainActivity.class.getSimpleName();

    @InjectView(R.id.hello_world_text_view)
    TextView textView;

    public MainActivity() {
        super(TimerState.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        postCreate();
    }

    private void postCreate() {
        bind(Observable.just(getState().timer.getTime())
                .map(Utils::millisecondsToSeconds)
                .map(Object::toString))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(textView::setText);

        bind(getState().timer.isRunning()
                .first()
                .filter(isRunning -> isRunning))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isRunning -> start());
    }

    public static class TimerState extends RetainedStateHelper.RetainedState {
        public final RxTicker timer = new RxTicker();
        public Observable<Long> timeObservable;
    }

    @OnClick(R.id.start_button)
    public void start() {
        getState().timeObservable = bind(getState().timer.start()
                .map(Utils::millisecondsToSeconds)
                .distinctUntilChanged());

        singleSubscription("start",
                getState().timeObservable
                        .map(Object::toString)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(textView::setText));

        getState().timeObservable
                .map(s -> Math.random())
                .map(Double::floatValue)
                .timeInterval()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(floatTimeInterval ->
                        textView.animate()
                                .alpha(floatTimeInterval.getValue())
                                .setDuration(floatTimeInterval.getIntervalInMilliseconds()));

    }

    @OnClick(R.id.stop_button)
    public void stop() {
        getState().timer.stop();
    }

    @OnClick(R.id.pause_button)
    public void pause() {
        getState().timer.pause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
