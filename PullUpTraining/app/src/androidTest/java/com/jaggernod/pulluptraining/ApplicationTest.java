package com.jaggernod.pulluptraining;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNotNull;

@Config(emulateSdk = 18)
@RunWith(RobolectricGradleTestRunner.class)
public class ApplicationTest {

    @Test
    public void testSomething() throws Exception {
        assertNotNull(true);
    }
}