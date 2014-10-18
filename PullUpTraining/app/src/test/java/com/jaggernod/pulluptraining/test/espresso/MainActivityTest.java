package com.jaggernod.pulluptraining.test.espresso;

import com.jaggernod.pulluptraining.R;
import com.jaggernod.pulluptraining.activities.MainActivity;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import android.content.pm.ActivityInfo;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;

/**
* Created by Pawel Polanski on 15/10/14.
*/
@LargeTest
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public MainActivityTest() {
        super(MainActivity.class);
    }
    @Override
    public void setUp() throws Exception {
        super.setUp();
        // Espresso will not launch our activity for us, we must launch it via getActivity().
        getActivity();
    }

    public void testCheckText() {
        onView(withId(R.id.hello_world_text_view))
                .check(matches(withText(R.string.hello_world)));
    }

    public void testStartButtonStartsCounting() {
        onView(withId(R.id.start_button))
                .perform(click());
        onView(withId(R.id.hello_world_text_view))
                .check(matches(withText(new BaseMatcher<String>() {

                    @Override
                    public boolean matches(Object o) {
                        return o instanceof String && ((String) o).matches("-?\\d+(\\.\\d+)?");
                    }

                    @Override
                    public void describeTo(Description description) {
                        description.appendText("a string with a number");
                    }
                })));
    }


    public void testRotateActivity() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

}