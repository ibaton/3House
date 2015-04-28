package treehou.se.habit.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;

import org.atmosphere.wasync.Client;
import org.atmosphere.wasync.ClientFactory;
import org.atmosphere.wasync.Decoder;
import org.atmosphere.wasync.Encoder;
import org.atmosphere.wasync.Event;
import org.atmosphere.wasync.Function;
import org.atmosphere.wasync.OptionsBuilder;
import org.atmosphere.wasync.RequestBuilder;
import org.atmosphere.wasync.Socket;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import treehou.se.habit.R;
import treehou.se.habit.connector.Communicator;
import treehou.se.habit.connector.TrustModifier;
import treehou.se.habit.core.LinkedPage;
import treehou.se.habit.core.Server;
import treehou.se.habit.core.Util;
import treehou.se.habit.core.Widget;
import treehou.se.habit.ui.widgets.WidgetFactory;

public class PageFragment extends Fragment {

    private static final String TAG = "PageFragment";
    private static final String PAGE_REQUEST_TAG = "PageRequestTag";

    // Arguments
    private static final String ARG_PAGE    = "ARG_PAGE";
    private static final String ARG_SERVER  = "ARG_SERVER";

    private SitemapFragment mSitemapFragment;
    private Server server;
    private LinkedPage mPage;

    private LinearLayout louFragments;

    private WidgetFactory widgetFactory;
    private List<Widget> widgets = new ArrayList<>();
    private List<WidgetFactory.IWidgetHolder> widgetHolders = new ArrayList<>();

    private Socket pollSocket;

    /**
     * Creates a new instane of the page.
     *
     * @param sitemapFragment
     * @param server the server to connect to
     * @param page the page to visualise
     * @return
     */
    public static PageFragment newInstance(SitemapFragment sitemapFragment, Server server, LinkedPage page) {
        Gson gson = Util.createGsonBuilder();

        Bundle args = new Bundle();
        args.putString(ARG_PAGE, gson.toJson(page));
        args.putLong(ARG_SERVER, server.getId());

        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        fragment.setSitemapFragment(sitemapFragment);

        Log.d(TAG, "Initialized " + page.getLink());

        return fragment;
    }

    public PageFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        Gson gson = Util.createGsonBuilder();

        long serverId = args.getLong(ARG_SERVER);
        server = Server.load(Server.class, serverId);

        String jPage = args.getString(ARG_PAGE);
        mPage = gson.fromJson(jPage, LinkedPage.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_widget, container, false);

        louFragments = (LinearLayout) view.findViewById(R.id.lou_widgets);
        updatePage(mPage);

        // Start listening for server updates
        longPoller.execute();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Stop listening for server updates
        longPoller.cancel(true);
    }

    private AsyncTask<Void, Void, Void> longPoller = new AsyncTask<Void, Void, Void>() {

        @Override
        protected Void doInBackground(Void... params) {

            String credentials = String.format("Basic %s:%s", server.getUsername(), server.getPassword());

            AsyncHttpClient asyncHttpClient = new AsyncHttpClient(
                new AsyncHttpClientConfig.Builder().setAcceptAnyCertificate(true)
                    .setHostnameVerifier(new TrustModifier.NullHostNameVerifier())
                    .build()
            );

            Client client = ClientFactory.getDefault().newClient();
            OptionsBuilder optBuilder = client.newOptionsBuilder().runtime(asyncHttpClient);

            UUID atmosphereId = UUID.randomUUID();

            RequestBuilder request = client.newRequestBuilder()
                .method(org.atmosphere.wasync.Request.METHOD.GET)
                .uri(mPage.getLink())
                .header("Authorization", credentials)
                .header("Accept", "application/json")
                .header("Accept-Charset", "utf-8")
                .header("X-Atmosphere-Transport", "long-polling")
                .header("X-Atmosphere-tracking-id", atmosphereId.toString())
                .encoder(new Encoder<String, Reader>() {        // Stream the request body
                    @Override
                    public Reader encode(String s) {
                        Log.d(TAG, "RequestBuilder encode");
                        return new StringReader(s);
                    }
                })
                .decoder(new Decoder<String, LinkedPage>() {
                    @Override
                    public LinkedPage decode(Event e, String s) {
                        Gson gson = Util.createGsonBuilder();
                        return gson.fromJson(s, LinkedPage.class);
                    }
                })
                .transport(org.atmosphere.wasync.Request.TRANSPORT.LONG_POLLING);                    // Fallback to Long-Polling

            pollSocket = client.create(optBuilder.build());
            try {
                Log.d(TAG, "Socket " + pollSocket + " " + request.uri());
                pollSocket.on(new Function<LinkedPage>() {
                    @Override
                    public void on(LinkedPage page) {
                        Log.d(TAG, "Socket received");
                        updatePage(page);
                    }
                })
                .open(request.build());
            } catch (IOException | ExceptionInInitializerError e) {
                Log.d(TAG, "Got error " + e);
            }

            Log.d(TAG,"Poller started");

            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

            if(pollSocket != null) {
                pollSocket.close();
            }
        }
    };


    @Override
    public void onResume() {
        super.onResume();

        Communicator communicator = Communicator.instance(getActivity());
        communicator.requestPage(PAGE_REQUEST_TAG, server, mPage.getLink(),
                new Response.Listener<LinkedPage>() {
                    @Override
                    public void onResponse(LinkedPage response) {
                        //TODO update instead of reset.
                        Log.d(TAG, "Received update " + response.getWidget().size() + " widgets from  " + mPage.getLink());
                        updatePage(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "error " + error.getCause() + " " + error.getMessage());

                        // TODO Check type of error.
                        // TODO Retry on remote server.
                        Toast.makeText(getActivity(), "Lost connection to server", Toast.LENGTH_LONG).show();
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                });
    }

    /**
     * Update page.
     *
     * Recreate all widgets needed.
     *
     * @param page
     */
    private synchronized void updatePage(final LinkedPage page){
        mPage = page;
        widgetFactory = new WidgetFactory(getActivity(), server, page);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                List<Widget> pageWidgets = page.getWidget();

                boolean invalidate = pageWidgets.size() != widgets.size();
                if(!invalidate){
                    for(int i=0; i < widgets.size(); i++) {
                        Widget currentWidget = widgets.get(i);
                        Widget newWidget = pageWidgets.get(i);

                        if(currentWidget.needUpdate(newWidget)){
                            invalidate = true;
                            break;
                        }
                    }
                }

                if(invalidate) {
                    Log.d(TAG, "Invalidating widgets " + pageWidgets.size() + " : " + widgets.size());

                    widgetHolders.clear();
                    louFragments.removeAllViews();
                    List<Widget> tWidgets = new ArrayList<>(widgets);

                    Log.d(TAG, "Added page views to " + page.getTitle() + " " + tWidgets.size());
                    for (Widget widget : pageWidgets) {
                        WidgetFactory.IWidgetHolder result = widgetFactory.createWidget(widget, null);
                        widgetHolders.add(result);
                        louFragments.addView(result.getView());
                    }
                    widgets.clear();
                    widgets.addAll(page.getWidget());
                }
                else {
                    Log.d(TAG, "updating widgets");
                    for (int i=0; i < widgetHolders.size(); i++) {
                        WidgetFactory.IWidgetHolder holder = widgetHolders.get(i);

                        Log.d(TAG, "updating widget " + holder.getClass().getSimpleName());
                        Widget newWidget = pageWidgets.get(i);

                        holder.update(newWidget);
                    }
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        Communicator communicator = Communicator.instance(getActivity());
        communicator.cancelRequest(PAGE_REQUEST_TAG);
    }

    public void setSitemapFragment(SitemapFragment sitemapFragment) {
        mSitemapFragment = sitemapFragment;
    }
}
