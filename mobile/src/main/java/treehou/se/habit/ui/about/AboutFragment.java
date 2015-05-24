package treehou.se.habit.ui.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import treehou.se.habit.R;
import treehou.se.habit.ui.adapter.ImageItem;
import treehou.se.habit.ui.adapter.ImageItemAdapter;

public class AboutFragment extends Fragment {

    private static final int ITEM_FEEDBACK      = 1;
    private static final int ITEM_GOOGLE_PLUS   = 2;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ImageItemAdapter mAdapter;

    public static AboutFragment newInstance() {
        AboutFragment fragment = new AboutFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AboutFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new ImageItemAdapter(getActivity());
        mAdapter.addItem(new ImageItem(ITEM_FEEDBACK, getActivity().getString(R.string.feedback), R.drawable.ic_item_icon_feedback));
        mAdapter.addItem(new ImageItem(ITEM_GOOGLE_PLUS, getActivity().getString(R.string.google_plus_community), R.drawable.icon_google_plus));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        RecyclerView mListView = (RecyclerView) view.findViewById(R.id.list);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        mListView.setLayoutManager(gridLayoutManager);
        mListView.setItemAnimator(new DefaultItemAnimator());
        mListView.setAdapter(mAdapter);

        mAdapter.setItemClickListener(new ImageItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int id) {
                switch (id) {
                    case ITEM_FEEDBACK:
                        Intent mailIntent = new Intent(Intent.ACTION_SENDTO);
                        mailIntent.setData(Uri.parse("mailto:"));
                        mailIntent.putExtra(Intent.EXTRA_SUBJECT, getActivity().getString(R.string.feedback));
                        mailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{getActivity().getString(R.string.email_feedback)});
                        startActivity(mailIntent);
                        break;
                    case ITEM_GOOGLE_PLUS:
                        Intent communityIntent = new Intent(Intent.ACTION_VIEW);
                        communityIntent.setData(Uri.parse("https://plus.google.com/u/2/communities/104057398315501111932"));
                        startActivity(communityIntent);
                        break;
                }
            }
        });

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(R.string.about);
        }

        return view;
    }
}
