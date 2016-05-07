package treehou.se.habit.ui.server;

import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import treehou.se.habit.DaggerActivityTestRule;
import treehou.se.habit.MainActivity;
import treehou.se.habit.NavigationUtil;
import treehou.se.habit.R;
import treehou.se.habit.TestUtil;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItem;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.AnyOf.anyOf;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class ServersTest {

    @Rule
    public DaggerActivityTestRule<MainActivity> activityRule = TestUtil.TestRule();

    @Before
    public void setup(){}

    private void createServer(String testServerName){
        onView(anyOf(allOf(withId(R.id.fab_add), isDisplayed()), allOf(withText(R.string.new_server), isDisplayed()))).perform(click());
        onView(withId(R.id.txt_server_name)).perform(ViewActions.typeText(testServerName));
        closeSoftKeyboard();
        pressBack();
    }

    private void deleteServer(String testServerName){
        onView(withId(R.id.list)).perform(RecyclerViewActions.scrollTo(hasDescendant(withText(testServerName))));
        onView(withId(R.id.list)).perform(actionOnItem(hasDescendant(withText(testServerName)), longClick()));
        onView(withText(R.string.delete)).perform(click());
        onView(withText(R.string.ok)).perform(click());
        onView(withId(R.id.txt_server_name)).check(doesNotExist());
    }

    @Test
    public void testCreateRemoveServer() {
        String testServerName = "Test Server " + new Random().nextInt(10000);
        NavigationUtil.navigateToServer();
        createServer(testServerName);
        deleteServer(testServerName);
    }

    @Test
    public void testCreateRemoveMultipleServer() {
        final int serversToCreate = 8;
        NavigationUtil.navigateToServer();
        String serverBaseName = "Test Server ";
        for(int i=0; i<serversToCreate; i++){
            createServer(serverBaseName+i);
        }

        for(int i=0; i<serversToCreate; i++){
            deleteServer(serverBaseName+i);
        }
    }
}