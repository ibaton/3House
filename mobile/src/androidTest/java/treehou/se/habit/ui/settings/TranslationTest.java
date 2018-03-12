package treehou.se.habit.ui.settings;

import android.content.Intent;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.matcher.IntentMatchers;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import treehou.se.habit.NavigationUtil;
import treehou.se.habit.R;
import treehou.se.habit.ui.main.MainActivity;
import treehou.se.habit.util.IntentHelper;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class TranslationTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setup(){
        Intents.init();
    }

    @After
    public void teardown(){
        Intents.release();
    }

    @Test
    public void testOpenSettings() {
        NavigationUtil.INSTANCE.navigateToSettings();
        onView(withText(R.string.help_translate)).perform(ViewActions.click());

        Intent intent = IntentHelper.helpTranslateIntent();
        Intents.intended(IntentMatchers.hasAction(intent.getAction()));
        Intents.intended(IntentMatchers.hasData(intent.getData()));
    }
}