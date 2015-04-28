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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import treehou.se.habit.R;
import treehou.se.habit.connector.Communicator;
import treehou.se.habit.core.LinkedPage;
import treehou.se.habit.core.Server;
import treehou.se.habit.core.Sitemap;
import treehou.se.habit.core.Util;
import treehou.se.habit.ui.homescreen.VoiceService;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class SitemapFragment extends Fragment {

    private static final String TAG = "SitemapFragment";
    private static final String ARG_SITEMAP = "ARG_SITEMAP";
    public static final String TAG_SITEMAP_FRAGMENT = "TAG_SITEMAP_FRAGMENT";

    public static final String TAG_PAGE_REQUEST = "TAG_PAGE_REQUEST";

    private Sitemap sitemap;
    private Communicator communicator;
    private SitemapAdapter sitemapAdapter;
    private ViewPager pgrSitemap;
    private ArrayList<LinkedPage> pages = new ArrayList<>();

    public static SitemapFragment newInstance(Sitemap sitemap){
        SitemapFragment fragment = new SitemapFragment();

        Bundle args = new Bundle();
        Gson gson = Util.createGsonBuilder();
        args.putString(ARG_SITEMAP, gson.toJson(sitemap));
        fragment.setArguments(args);

        return fragment;
    }

    public SitemapFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        Gson gson = Util.createGsonBuilder();

        communicator = Communicator.instance(getActivity());
        sitemap = gson.fromJson(getArguments().getString(ARG_SITEMAP), Sitemap.class);
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
        actionBar.setTitle(sitemap.getName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_sitemap, container, false);

        sitemapAdapter = new SitemapAdapter(sitemap.getServer(), this, getActivity().getSupportFragmentManager(), pages);
        pgrSitemap = (ViewPager) rootView.findViewById(R.id.pgr_sitemap);
        pgrSitemap.setAdapter(sitemapAdapter);
        pgrSitemap.setOnPageChangeListener(pagerChangeListener);

        if(pages.size() == 0) {
            communicator.requestPage(TAG_PAGE_REQUEST, sitemap.getServer(), sitemap.getHomepage().getLink(),
                new Response.Listener<LinkedPage>() {
                    @Override
                    public void onResponse(LinkedPage page) {
                        pages.add(page);
                        sitemapAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {}
                });
        }
        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    // Removes history when moving back in tabs
    private ViewPager.OnPageChangeListener pagerChangeListener = new ViewPager.OnPageChangeListener() {
        int index = 0;

        @Override
        public void onPageScrolled(int i, float v, int i2) {}

        @Override
        public void onPageSelected(int i) {index=i;}

        @Override
        public void onPageScrollStateChanged(int state) {
            if(state == ViewPager.SCROLL_STATE_IDLE){
                while(pages.size() > index+1) {
                    pages.remove(pages.size()-1);
                }
                sitemapAdapter.notifyDataSetChanged();
            }
        }
    };

    /**
     * Add and move to page in view pager.
     * @param page the page to add to pager
     */
    public void addPage(LinkedPage page){
        Log.d(TAG,"Add page4 " + page.getLink());
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
                openVoiceCommand(sitemap.getServer());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    public void openVoiceCommand(Server server){
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

    public void onEvent(LinkedPage event){
        addPage(event);
    }
}
