package com.jaggernod.pulluptraining.test.utils;

import com.jaggernod.pulluptraining.test.RobolectricGradleTestRunner;
import com.jaggernod.pulluptraining.utils.TimerPullUp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import android.os.Bundle;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import rx.Observer;
import rx.functions.Action1;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Created by Pawel Polanski on 14/10/14.
 */

@Config(emulateSdk = 18)
@RunWith(RobolectricGradleTestRunner.class)
public class TimerPullUpTest {

    TimerPullUp timer;

    @Before
    public void setUp() throws Exception {
        timer = new TimerPullUp();
    }

    @Test
    public void testTimerNotNullWhenCreated() throws Exception {
        assertNotNull(timer);
    }

    @Test
    public void testTimerNotStartedWhenCreated() throws Exception {
        timer.isRunning()
                .timeout(100, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        assertFalse(aBoolean);
                    }
                });
    }

    @Test
    public void testTimerNotStartedWhenNotSubscribed() throws Exception {
        timer.start();
        timer.isRunning()
                .timeout(100, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        assertFalse(aBoolean);
                    }
                });
    }

    @Test
    public void testTimerStartedWhenSubscribed() throws Exception {
        timer.start().subscribe();
        timer.isRunning()
                .timeout(100, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        assertTrue(aBoolean);
                    }
                });
    }

    @Test
    public void testStoppedWhenStopped() throws Exception {
        timer.start().subscribe();
        timer.pause();
        timer.isRunning()
                .timeout(100, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        assertFalse(aBoolean);
                    }
                });
    }

    @Test
    public void testReceivingWhenSubscribed() {
        final CountDownLatch latch = new CountDownLatch(1);
        timer.start().subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                latch.countDown();
            }
        });

        try {
            assertTrue(latch.await(100, TimeUnit.MILLISECONDS));
        } catch (InterruptedException e) {
            fail("Should have received an event");
        }
    }

    @Test
    public void testCompletedWhenStopped() {
        final CountDownLatch latch = new CountDownLatch(1);
        timer.start().subscribe(new Observer<Long>() {
            @Override
            public void onCompleted() {
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Long aLong) {

            }
        });
        timer.pause();
        try {
            assertTrue(latch.await(100, TimeUnit.MILLISECONDS));
        } catch (InterruptedException e) {
            fail("Should have not failed");
        }
    }

    @Test
    public void testIsParcelable() throws Exception {
        Bundle bundle = new Bundle();
        bundle.putParcelable("1", timer);
        assertNotNull(bundle.getParcelable("1"));
    }

    @Test
    public void testRestoredParcelIsTheSame() throws Exception {
        Bundle bundle = new Bundle();
        bundle.putParcelable("1", timer);
        assertTrue(bundle.getParcelable("1").equals(timer));
    }

    @Test
    public void testPausedResumes() throws Exception {
        timer.start().subscribe();
        Thread.sleep(100);
        timer.pause();
        assertNotEquals(timer.getTime(), -1);
    }

    @Test
    public void testStoppedDoesNotResume() throws Exception {
        timer.start().subscribe();
        Thread.sleep(100);
        timer.stop();
        assertEquals(timer.getTime(), 0);
    }
}