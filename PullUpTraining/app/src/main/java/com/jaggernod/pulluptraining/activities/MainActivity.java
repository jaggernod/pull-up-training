package com.jaggernod.pulluptraining.activities;

import com.jaggernod.pulluptraining.R;
import com.jaggernod.pulluptraining.TimerPullUp;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static rx.Observable.OnSubscribe;

/**
 * Created by Pawel Polanski on 14/10/14.
 */

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String ELAPSED_TIME = "ELAPSED_TIME";

    @InjectView(R.id.hello_world_text_view)
    TextView textView;

    private TimerPullUp timer = new TimerPullUp();

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
            timer = savedInstanceState.getParcelable(ELAPSED_TIME);
        } else {
            // Probably initialize members with default values for a new instance
        }

        postCreate();
    }

    private void postCreate() {
        if (timer.isRunning()) {
            test1();
        }
    }

    //    @OnClick(R.id.start_button)
    public void test() {
        clearSubscriptions();
        Observable<String> observable = Observable.create(new Ticker())
                .map(Object::toString)
                .doOnNext(s -> Log.d(TAG, String.valueOf(((Object)this).hashCode())));
        registerSubscription(
                observable
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(textView::setText));
    }

    @OnClick(R.id.start_button)
    public void test1() {
        clearSubscriptions();
        Observable<String> observable = timer.start()
                .map(aLong -> Math.round(aLong / 10.))
                .map(Object::toString);
        registerSubscription(
                observable
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(textView::setText));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(ELAPSED_TIME, timer);
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

    private static class Ticker implements OnSubscribe<Integer> {

        @Override
        public void call(Subscriber<? super Integer> subscriber) {
            if (!subscriber.isUnsubscribed()) {
                int i = 0;
                while (i < 100000) {
                    subscriber.onNext(i++);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        subscriber.onError(e);
                        break;
                    }
                }
                subscriber.onCompleted();
            }
        }
    }
}
