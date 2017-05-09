package treehou.se.habit.ui.sitemaps.page;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.util.GsonHelper;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.module.HasActivitySubcomponentBuilders;
import treehou.se.habit.mvp.BaseDaggerFragment;
import treehou.se.habit.ui.widgets.WidgetFactory;

public class PageFragment extends BaseDaggerFragment<PageContract.Presenter> implements PageContract.View {

    private static final String TAG = "PageFragment";

    @BindView(R.id.lou_widgets) LinearLayout louWidgets;

    @Inject PageContract.Presenter presenter;

    private Unbinder unbinder;

    /**
     * Creates a new instane of the page.
     *
     * @param server the server to connect to
     * @param page the page to visualise
     *
     * @return Fragment visualazing a page
     */
    public static PageFragment newInstance(ServerDB server, OHLinkedPage page) {
        Gson gson = GsonHelper.createGsonBuilder();

        Bundle args = new Bundle();
        args.putString(PageContract.ARG_PAGE, gson.toJson(page));
        args.putLong(PageContract.ARG_SERVER, server.getId());

        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public PageFragment() {}

    @Override
    public void showLostServerConnectionMessage() {
        Toast.makeText(getActivity(), R.string.lost_server_connection, Toast.LENGTH_LONG).show();
    }

    @Override
    public void closeView() {
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public PageContract.Presenter getPresenter() {
        return presenter;
    }

    @Override
    protected void injectMembers(HasActivitySubcomponentBuilders hasActivitySubcomponentBuilders) {
        ((PageComponent.Builder) hasActivitySubcomponentBuilders.getFragmentComponentBuilder(PageFragment.class))
                .fragmentModule(new PageModule(this, getArguments()))
                .build().injectMembers(this);
    }

    @Override
    public void updatePage(OHLinkedPage page) {
        setupActionbar(page);
    }

    private void removeAllWidgets() {
        louWidgets.removeAllViews();
    }

    @Override
    public void setWidgets(List<WidgetFactory.IWidgetHolder> widgets) {
        removeAllWidgets();

        for (WidgetFactory.IWidgetHolder widget : widgets) {
            louWidgets.addView(widget.getView());
        }
    }

    /**
     * Setup actionbar using
     */
    private void setupActionbar(OHLinkedPage page){
        ActionBar actionBar = null;
        if(isAdded()) {
            actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        }
        String title = page.getTitle();
        if(title == null) title = "";
        title = removeValueFromTitle(title);

        if(actionBar != null) actionBar.setTitle(title);
    }

    private String removeValueFromTitle(String title){
        return title.replaceAll("\\[.+?\\]","");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_widget, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
