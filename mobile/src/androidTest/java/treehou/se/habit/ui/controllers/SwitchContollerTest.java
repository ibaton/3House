package treehou.se.habit.ui.controllers;

import android.app.Activity;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import treehou.se.habit.DaggerActivityTestRule;
import treehou.se.habit.HabitApplication;
import treehou.se.habit.NavigationUtil;
import treehou.se.habit.R;
import treehou.se.habit.dagger.ApplicationComponent;
import treehou.se.habit.dagger.DaggerApplicationComponent;
import treehou.se.habit.data.TestAndroidModule;
import treehou.se.habit.ui.main.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class SwitchContollerTest {

    @Rule
    public DaggerActivityTestRule<MainActivity> activityRule = new DaggerActivityTestRule<MainActivity>(MainActivity.class) {
        @Override
        public ApplicationComponent setupComponent(HabitApplication application, Activity activity) {
            return createComponent(application);
        }
    };


    @Before
    public void setup(){}

    @Test
    public void testIconSet() {
        NavigationUtil.INSTANCE.navigateToController();

        onView(withId(R.id.fab_add)).perform(click());
        onView(withId(R.id.btn_add_column)).perform(click());
        onView(withId(R.id.img_icon_button)).perform(click());
        onView(withText(R.string.empty)).perform(click());
        onView(withText(R.string.label_switch)).perform(click());
        onView(withId(R.id.btn_set_icon)).perform(click());

        onView(allOf(withId(R.id.img_menu), hasSibling(withText(R.string.media)))).check(matches(isDisplayed()));
    }

    private ApplicationComponent createComponent(HabitApplication application){
        ApplicationComponent component = DaggerApplicationComponent.builder()
                .androidModule(new TestAndroidModule(application){}).build();

        return component;
    }
}