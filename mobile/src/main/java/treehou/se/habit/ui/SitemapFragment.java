package treehou.se.habit.ui;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import se.treehou.ng.ohcommunicator.Openhab;
import se.treehou.ng.ohcommunicator.connector.GsonHelper;
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap;
import se.treehou.ng.ohcommunicator.services.callbacks.OHCallback;
import se.treehou.ng.ohcommunicator.services.callbacks.OHResponse;
import treehou.se.habit.R;
import treehou.se.habit.connector.Communicator;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.ui.homescreen.VoiceService;

public class SitemapFragment extends Fragment {

    private static final String TAG = "SitemapFragment";
    private static final String ARG_SITEMAP = "ARG_SITEMAP";
    private static final String ARG_SERVER = "ARG_SERVER";

    private Realm realm;

    private ServerDB server;
    private OHSitemap sitemap;
    private Communicator communicator;
    private SitemapAdapter sitemapAdapter;
    private ViewPager pgrSitemap;
    private ArrayList<OHLinkedPage> pages = new ArrayList<>();

    private OHCallback<OHLinkedPage> requestPageCallback = new RequestPageDummyListener();

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

        realm = Realm.getDefaultInstance();

        communicator = Communicator.instance(getActivity());

        long serverId = getArguments().getLong(ARG_SERVER);
        server = ServerDB.load(realm, serverId);

        String jSitemap = getArguments().getString(ARG_SITEMAP);
        sitemap = GsonHelper.createGsonBuilder().fromJson(jSitemap, OHSitemap.class);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(sitemap.getLabel());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_sitemap, null);

        sitemapAdapter = new SitemapAdapter(server, getActivity().getSupportFragmentManager(), pages);
        pgrSitemap = (ViewPager) rootView.findViewById(R.id.pgr_sitemap);
        pgrSitemap.setAdapter(sitemapAdapter);
        pgrSitemap.addOnPageChangeListener(pagerChangeListener);

        requestPageCallback = new OHCallback<OHLinkedPage>() {
            @Override
            public void onUpdate(OHResponse<OHLinkedPage> items) {
                OHLinkedPage linkedPage = items.body();
                Log.d(TAG, "Received page " + linkedPage);
                pages.add(linkedPage);
                sitemapAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError() {
                Log.d(TAG, "Received page failed");
            }
        };

        if(pages.size() == 0) {
            Log.d(TAG, "Requesting page");
            Openhab.instance(sitemap.getServer()).requestPage(sitemap.getHomepage(), requestPageCallback);
        }
        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        requestPageCallback = new RequestPageDummyListener();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        if(pages.size() > 0 && getActivity() instanceof AppCompatActivity){
            ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle("");
        }
        super.onStop();
    }

    /**
     * Handle callbacks for request page.
     */
    interface RequestPageCallback {
        void success(OHLinkedPage linkedPage, retrofit.client.Response response);
        void failure(RetrofitError error);
    }

    class RequestPageDummyListener implements OHCallback<OHLinkedPage> {

        @Override
        public void onUpdate(OHResponse<OHLinkedPage> items) {}

        @Override
        public void onError() {}
    }

    // Removes history when moving back in tabs
    private ViewPager.OnPageChangeListener pagerChangeListener = new ViewPager.OnPageChangeListener() {
        int index = 0;

        @Override
        public void onPageScrolled(int i, float v, int i2) {}

        @Override
        public void onPageSelected(int i) {
            index=i;
            if(pages.size() > 0 && getActivity() instanceof AppCompatActivity){
                // TODO ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(Html.fromHtml(pages.get(i).getActionbarTitle()));
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            // Remove tabs that not parent of selected view
            if(state == ViewPager.SCROLL_STATE_IDLE){
                while(pages.size() > index+1) {
                    pages.remove(pages.size() - 1);
                    sitemapAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    /**
     * Add and move to page in view pager.
     *
     * @param page the page to add to pager
     */
    public void addPage(OHLinkedPage page) {
        Log.d(TAG, "Add page " + page.getLink());
        pages.add(page);
        sitemapAdapter.notifyDataSetChanged();
        pgrSitemap.setCurrentItem(pages.size() - 1, true);
    }

    /**
     * Move one step back in viewpager and remove page.
     *
     * @return true if fragment was poped.
     */
    public boolean popStack(){
        if(pages.size() > 1) {
            pgrSitemap.setCurrentItem(pages.size() - 2);
            return true;
        }
        return false;
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
        Log.d("SitemapFragment", "SitemapFragment destroyed");

        realm.close();
    }
}
