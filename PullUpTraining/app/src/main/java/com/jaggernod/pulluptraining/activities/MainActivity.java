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

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @InjectView(R.id.hello_world_text_view)
    TextView textView;

    private TimerState state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        state = getRetainedObject(TimerState.class);

        postCreate();
    }

    private void postCreate() {
        bind(Observable.just(state.timer.getTime()))
                .map(Utils::millisecondsToSeconds)
                .map(Object::toString)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(textView::setText);

        bind(state.timer.isRunning())
                .first()
                .filter(isRunning -> isRunning)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isRunning -> start());
    }

    public static class TimerState extends RetainedStateHelper.RetainedState {
        public RxTicker timer = new RxTicker();
        public Observable<String> timeObservable;
    }

    @OnClick(R.id.start_button)
    public void start() {
        state.timeObservable = state.timer.start()
                .map(Utils::millisecondsToSeconds)
                .map(Object::toString);

        singleSubscription("start",
                bind(state.timeObservable)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(textView::setText));
    }

    @OnClick(R.id.stop_button)
    public void stop() {
        state.timer.stop();
    }

    @OnClick(R.id.pause_button)
    public void pause() {
        state.timer.pause();
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
