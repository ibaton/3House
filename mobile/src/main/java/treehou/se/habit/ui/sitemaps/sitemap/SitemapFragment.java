package treehou.se.habit.ui.sitemaps.sitemap;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
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

import javax.inject.Inject;

import io.realm.Realm;
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap;
import se.treehou.ng.ohcommunicator.util.GsonHelper;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.module.HasActivitySubcomponentBuilders;
import treehou.se.habit.mvp.BaseDaggerFragment;
import treehou.se.habit.ui.homescreen.VoiceService;
import treehou.se.habit.ui.sitemaps.page.PageFragment;
import treehou.se.habit.ui.sitemaps.sitemap.SitemapContract.Presenter;

public class SitemapFragment extends BaseDaggerFragment<Presenter> implements SitemapContract.View{

    private static final String TAG = "SitemapFragment";

    @Inject Presenter presenter;
    @Inject ServerDB server;
    @Inject OHSitemap sitemap;

    /**
     * Creates a new instance of fragment showing sitemap.
     *
     * @param server the server to use to open sitemap.
     * @param sitemap the sitemap to load.
     * @return Fragment displaying sitemap.
     */
    public static SitemapFragment newInstance(OHServer server, OHSitemap sitemap){
        ServerDB serverDB = Realm.getDefaultInstance()
                .where(ServerDB.class)
                .equalTo("name", server.getName())
                .findFirst();

        return newInstance(serverDB, sitemap);
    }

    /**
     * Creates a new instance of fragment showing sitemap.,
     *
     * @param serverDB the server to use to open sitemap.
     * @param sitemap the sitemap to load.
     * @return Fragment displaying sitemap.
     */
    public static SitemapFragment newInstance(ServerDB serverDB, OHSitemap sitemap){
        SitemapFragment fragment = new SitemapFragment();

        Bundle args = new Bundle();
        args.putString(Presenter.ARG_SITEMAP, GsonHelper.createGsonBuilder().toJson(sitemap));
        args.putLong(Presenter.ARG_SERVER, serverDB.getId());
        fragment.setArguments(args);

        return fragment;
    }

    public SitemapFragment() {}

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

        setHasOptionsMenu(isVoiceCommandSupported(server));

        return rootView;
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
    @Override
    public boolean hasPage(){
        return getChildFragmentManager().getBackStackEntryCount() > 0;
    }

    /**
     * Add and move to page in view pager.
     *
     * @param page the page to add to pager
     */
    @Override
    public void showPage(ServerDB server, OHLinkedPage page) {
        Log.d(TAG, "Add page " + page.getLink());
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
        if(isVoiceCommandSupported(server)) {
            startActivity(createVoiceCommandIntent(server));
        }
    }

    /**
     * Creates an intent use to input voice command.
     * @return intent used to fire voice command
     */
    private Intent createVoiceCommandIntent(ServerDB server){
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
        return intent;
    }

    /**
     * Check if cvoice command is supported by device.
     * @return true if supported, else false
     */
    private boolean isVoiceCommandSupported(ServerDB server){
        PackageManager packageManager = getContext().getPackageManager();
        return packageManager.resolveActivity(createVoiceCommandIntent(server), 0) != null;
    }

    /**
     * Pop backstack
     * @return true if handled by fragment, else false.
     */
    @Override
    public boolean removeAllPages(){
        FragmentManager fragmentManager = getChildFragmentManager();
        int backStackEntryCount = fragmentManager.getBackStackEntryCount();
        if(backStackEntryCount > 0){
            fragmentManager.popBackStackImmediate();
        }
        backStackEntryCount = fragmentManager.getBackStackEntryCount();

        return backStackEntryCount >= 1;
    }

    @Override
    public Presenter getPresenter() {
        return presenter;
    }

    @Override
    protected void injectMembers(HasActivitySubcomponentBuilders hasActivitySubcomponentBuilders) {
        ((SitemapComponent.Builder) hasActivitySubcomponentBuilders.getFragmentComponentBuilder(SitemapFragment.class))
                .fragmentModule(new SitemapModule(this, getArguments()))
                .build().injectMembers(this);
    }
}
