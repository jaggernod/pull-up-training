package com.jaggernod.pulluptraining;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView textView;

    private CompositeSubscription subscriptions = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.hello_world_text_view);
        subscriptions.add(
                Observable.create(new Ticker())
                        .map(Object::toString)
                        .doOnNext(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                Log.d(TAG, this.hashCode() + " " + s);
                            }
                        })
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(textView::setText));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscriptions.clear();
    }

    private static class Ticker implements Observable.OnSubscribe<Integer> {

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
