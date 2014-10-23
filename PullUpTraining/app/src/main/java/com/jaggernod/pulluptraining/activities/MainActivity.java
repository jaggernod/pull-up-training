package com.jaggernod.pulluptraining.activities;

import com.jaggernod.pulluptraining.R;
import com.jaggernod.pulluptraining.utils.RxTimer;
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

    private static final String TIMER = "TIMER";

    @InjectView(R.id.hello_world_text_view)
    TextView textView;

    private RxTimer timer = new RxTimer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.inject(this);

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            timer = savedInstanceState.getParcelable(TIMER);
        }

        postCreate();
    }

    private void postCreate() {
        registerSubscription("setText",
                Observable.just(timer.getTime())
                        .map(Utils::millisecondsToSeconds)
                        .map(Object::toString)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(textView::setText));

        registerSubscription("isRunning",
                timer.isRunning()
                        .first()
                        .filter(isRunning -> isRunning)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(isRunning -> start()));
    }

    @OnClick(R.id.start_button)
    public void start() {
        registerSubscription("timer",
                timer.start()
                        .map(Utils::millisecondsToSeconds)
                        .map(Object::toString)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(textView::setText));
    }

    @OnClick(R.id.stop_button)
    public void stop() {
        timer.stop();
    }

    @OnClick(R.id.pause_button)
    public void pause() {
        timer.pause();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(TIMER, timer);
        super.onSaveInstanceState(savedInstanceState);
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
