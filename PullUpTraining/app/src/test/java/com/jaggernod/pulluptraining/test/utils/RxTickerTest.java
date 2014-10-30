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

package com.jaggernod.pulluptraining.test.utils;

import com.jaggernod.pulluptraining.test.RobolectricGradleTestRunner;
import com.jaggernod.pulluptraining.utils.RxTicker;

import junit.framework.Assert;

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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Created by Pawel Polanski on 14/10/14.
 */

@Config(emulateSdk = 18)
@RunWith(RobolectricGradleTestRunner.class)
public class RxTickerTest {

    RxTicker timer;

    @Before
    public void setUp() throws Exception {
        timer = new RxTicker();
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

        Assert.assertEquals(timer.getTime(), 0);
    }

    @Test
    public void testStoppedDoesNotResume() throws Exception {
        timer.start().subscribe();
        Thread.sleep(100);
        timer.stop();
        assertEquals(timer.getTime(), 0);
    }

}