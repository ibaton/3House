package treehou.se.habit.ui.settings.subsettings;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import treehou.se.habit.R;
import treehou.se.habit.core.Sitemap;
import treehou.se.habit.core.db.SitemapDB;
import treehou.se.habit.ui.SitemapSelectorFragment;
import treehou.se.habit.util.PrefSettings;

public class SitemapFragment extends Fragment implements SitemapSelectorFragment.OnSitemapSelectListener {

    private static final int REQUEST_SITEMAP = 3;
    private Button btnDefaultSitemap;

    public static SitemapFragment newInstance() {
        SitemapFragment fragment = new SitemapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SitemapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_settings_sitemaps, container, false);

        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(R.string.sitemaps);

        btnDefaultSitemap = (Button) rootView.findViewById(R.id.btnSetDefaultSitemap);

        CheckBox cbxDefaultSitemaps = (CheckBox) rootView.findViewById(R.id.cbx_default_sitemap);
        cbxDefaultSitemaps.setChecked(PrefSettings.instance(getActivity()).getUseDefaultSitemap());

        SitemapDB sitemapDB = PrefSettings.instance(getActivity()).getDefaultSitemap();
        if(sitemapDB != null) {
            btnDefaultSitemap.setText(sitemapDB.getName());
            btnDefaultSitemap.setVisibility(View.VISIBLE);
        }else {
            btnDefaultSitemap.setText(R.string.default_sitemap);
            btnDefaultSitemap.setVisibility(View.GONE);
        }

        cbxDefaultSitemaps.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    PrefSettings.instance(getActivity()).setDefaultSitemap(null);
                    btnDefaultSitemap.setText(R.string.default_sitemap);
                }
                btnDefaultSitemap.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });

        btnDefaultSitemap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = SitemapSelectorFragment.newInstance();
                fragment.setTargetFragment(SitemapFragment.this, REQUEST_SITEMAP);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(container.getId(), fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onSitemapSelect(Sitemap sitemap) {
        SitemapDB sitemapDB = new SitemapDB(sitemap);
        sitemapDB.save();
        PrefSettings.instance(getActivity()).setDefaultSitemap(sitemapDB);

        if(getActivity() != null) {
            btnDefaultSitemap.setText(sitemap.getName());
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }
}
