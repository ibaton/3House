package treehou.se.habit.ui.sitemaps;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import se.treehou.ng.ohcommunicator.Openhab;
import se.treehou.ng.ohcommunicator.connector.GsonHelper;
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap;
import se.treehou.ng.ohcommunicator.services.callbacks.OHCallback;
import se.treehou.ng.ohcommunicator.services.callbacks.OHResponse;
import treehou.se.habit.HabitApplication;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.ui.homescreen.VoiceService;

public class SitemapFragment extends Fragment {

    private static final String TAG = "SitemapFragment";
    private static final String ARG_SITEMAP = "ARG_SITEMAP";
    private static final String ARG_SERVER = "ARG_SERVER";

    @Inject Gson gson;

    private Realm realm;
    private ServerDB server;
    private OHSitemap sitemap;

    /**
     * Creates a new instance of fragment showing sitemap.
     *
     * @param serverDB the server to use to open sitemap.
     * @param sitemap the sitemap to load.
     * @return Fragment displaying sitemap.
     */
    public static SitemapFragment newInstance(ServerDB serverDB, OHSitemap sitemap){
        SitemapFragment fragment = new SitemapFragment();

        Bundle args = new Bundle();
        args.putString(ARG_SITEMAP, GsonHelper.createGsonBuilder().toJson(sitemap));
        args.putLong(ARG_SERVER, serverDB.getId());
        fragment.setArguments(args);

        return fragment;
    }

    public SitemapFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getComponent().inject(this);
        realm = Realm.getDefaultInstance();
        long serverId = getArguments().getLong(ARG_SERVER);
        server = ServerDB.load(realm, serverId);
        String jSitemap = getArguments().getString(ARG_SITEMAP);
        sitemap = gson.fromJson(jSitemap, OHSitemap.class);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupActionbar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_sitemap, container, false);
        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(!hasPage()) {
            Openhab.instance(sitemap.getServer()).requestPage(sitemap.getHomepage(), requestPageCallback);
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    protected HabitApplication.ApplicationComponent getComponent() {
        return ((HabitApplication) getActivity().getApplication()).component();
    }

    /**
     * Setup actionbar using
     */
    private void setupActionbar(){
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(actionBar != null) actionBar.setTitle(sitemap.getLabel());
    }

    /**
     * Check if a page is loaded.
     * @return true if page loaded, else false.
     */
    private boolean hasPage(){
        return getChildFragmentManager().getBackStackEntryCount() > 0;
    }

    /**
     * Add and move to page in view pager.
     *
     * @param page the page to add to pager
     */
    public void addPage(OHLinkedPage page) {
        FragmentManager fragmentManager = getChildFragmentManager();
        if(fragmentManager == null) return;

        Log.d(TAG, "Add page " + page.getLink());
        getChildFragmentManager().beginTransaction()
            .replace(R.id.pgr_sitemap, PageFragment.newInstance(server, page))
            .addToBackStack(null)
            .commit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.sitemap, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_voice_command:
                openVoiceCommand(server);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Start voice command listener.
     *
     * @param server server to send command to.
     */
    public void openVoiceCommand(ServerDB server){
        Intent callbackIntent = VoiceService.createVoiceCommand(getActivity(), server);

        PendingIntent openhabPendingIntent = PendingIntent.getService(getActivity(), 9, callbackIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // Specify the calling package to identify your application
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, VoiceService.class.getPackage().getName());
        // Display an hint to the user about what he should say.
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.voice_command_title));

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra(RecognizerIntent.EXTRA_RESULTS_PENDINGINTENT, openhabPendingIntent);

        startActivity(intent);
    }

    /**
     * Pop backstack
     * @return true if handled by fragment, else false.
     */
    public boolean popStack(){
        FragmentManager fragmentManager = getChildFragmentManager();
        int backStackEntryCount = fragmentManager.getBackStackEntryCount();
        if(backStackEntryCount > 0){
            fragmentManager.popBackStackImmediate();
        }
        backStackEntryCount = fragmentManager.getBackStackEntryCount();

        return backStackEntryCount >= 1;
    }

    /**
     * User requested to move to new page.
     *
     * @param event
     */
    public void onEvent(OHLinkedPage event){
        addPage(event);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
    
    private OHCallback<OHLinkedPage> requestPageCallback = new OHCallback<OHLinkedPage>() {
        @Override
        public void onUpdate(OHResponse<OHLinkedPage> items) {
            if (isDetached()) return; // TODO remove callback

            OHLinkedPage linkedPage = items.body();
            Log.d(TAG, "Received page " + linkedPage);
            addPage(linkedPage);
        }

        @Override
        public void onError() {
            Log.d(TAG, "Received page failed");
        }
    };
}
