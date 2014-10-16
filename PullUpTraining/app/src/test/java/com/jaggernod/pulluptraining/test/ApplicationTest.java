package com.jaggernod.pulluptraining.test;

import com.jaggernod.pulluptraining.helpers.StrictModeHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Pawel Polanski on 14/10/14.
 */

@Config(emulateSdk = 18)
@RunWith(RobolectricGradleTestRunner.class)
public class ApplicationTest {

    @Test
    public void testSomething() throws Exception {
        assertNotNull(true);
        StrictModeHelper.activityRecreation();

    }
}