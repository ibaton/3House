package treehou.se.habit.ui.settings;

import static android.support.test.espresso.matcher.ViewMatchers.withText;

/*@RunWith(AndroidJUnit4.class)
@SmallTest*/
public class GeneralTest {

    /*@Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setup(){
        Settings.instance(activityRule.getActivity()).setAutoloadSitemapRx(false);
        Settings.instance(activityRule.getActivity()).getFullscreenPref().set(false);
    }*/

    /**
     * Navigate to general settings.
     */
    /*public void navigateToGeneral(){
        NavigationUtil.navigateToSettings();
        onView(withText(R.string.settings_general)).perform(ViewActions.click());
    }

    @Test
    public void testOpenSettings() {
        navigateToGeneral();
        ViewInteraction cbxLoadLast = onView(withText(R.string.open_last_sitemap_on_upstart));
        cbxLoadLast.check(ViewAssertions.matches(CoreMatchers.not(isChecked())));
        cbxLoadLast.perform(ViewActions.click());
        cbxLoadLast.check(ViewAssertions.matches(isChecked()));
        cbxLoadLast.perform(ViewActions.click());
        cbxLoadLast.check(ViewAssertions.matches(CoreMatchers.not(isChecked())));
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Test
    public void testSetFullscreen() {
        navigateToGeneral();
        ViewInteraction cbxFullscreen = onView(withText(R.string.fullscreen));
        Assert.assertTrue("Expected !fullscreen", !isFullscreen());
        cbxFullscreen.check(ViewAssertions.matches(CoreMatchers.not(isChecked())));
        cbxFullscreen.perform(ViewActions.click());
        Assert.assertTrue("Expected fullscreen", isFullscreen());
        cbxFullscreen.check(ViewAssertions.matches(isChecked()));
        cbxFullscreen.perform(ViewActions.click());
        Assert.assertTrue("Expected !fullscreen", !isFullscreen());
        cbxFullscreen.check(ViewAssertions.matches(CoreMatchers.not(isChecked())));
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean isFullscreen(){
        View decorView = activityRule.getActivity().getWindow().getDecorView();
        return ((decorView.getSystemUiVisibility() & View.SYSTEM_UI_FLAG_FULLSCREEN) > 0);
    }*/
}