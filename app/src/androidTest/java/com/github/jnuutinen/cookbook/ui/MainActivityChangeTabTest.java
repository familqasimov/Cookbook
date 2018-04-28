package com.github.jnuutinen.cookbook.ui;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.github.jnuutinen.cookbook.R;
import com.github.jnuutinen.cookbook.presentation.main.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityChangeTabTest {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(
            MainActivity.class);

    @Test
    public void mainActivityChangeTab_CycleThroughTabs() {
        onData(withId(R.id.tab_recipes))
                .perform(click());
        onData(withId(R.id.tab_categories))
                .perform(click());
    }
}
