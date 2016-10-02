package treehou.se.habit.ui.servers;

import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.trello.rxlifecycle.components.support.RxFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.realm.Realm;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.ServerDB;

public class SetupServerFragment extends RxFragment {

    private static final String ARG_SERVER = "ARG_SERVER";
    public static final String ARG_BUTTON_TEXT_ID = "ARG_BUTTON_TEXT_ID";

    private static final String EXTRA_SERVER_ID = "EXTRA_SERVER_ID";

    @BindView(R.id.server_name_text) EditText txtName;
    @BindView(R.id.server_local_text) EditText localUrlText;
    @BindView(R.id.error_local_url) TextView errorLocalUrlText;
    @BindView(R.id.txt_server_remote) EditText remoteUrlText;
    @BindView(R.id.error_remote_url) TextView errorRemoteUrlText;
    @BindView(R.id.txt_username) EditText txtUsername;
    @BindView(R.id.txt_password) EditText txtPassword;
    @BindView(R.id.btn_back) Button btnBack;

    private long serverId = -1;
    private int buttonTextId = R.string.back;
    private Unbinder unbinder;

    public static SetupServerFragment newInstance() {
        SetupServerFragment fragment = new SetupServerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static SetupServerFragment newInstance(long serverId) {
        SetupServerFragment fragment = new SetupServerFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_SERVER, serverId);
        fragment.setArguments(args);
        return fragment;
    }

    public SetupServerFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle bundle = getArguments();

        if(savedInstanceState != null && savedInstanceState.containsKey(EXTRA_SERVER_ID)){
            serverId = savedInstanceState.getLong(EXTRA_SERVER_ID);
        }else if (bundle != null) {
            if (bundle.containsKey(ARG_SERVER)) serverId = bundle.getLong(ARG_SERVER);
            buttonTextId = bundle.getInt(ARG_BUTTON_TEXT_ID, R.string.back);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_setup_server, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        btnBack.setText(buttonTextId);

        return rootView;
    }

    @OnClick(R.id.btn_back)
    void onBack(){
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onResume() {
        super.onResume();

        Realm realm = Realm.getDefaultInstance();
        ServerDB server = realm.where(ServerDB.class).equalTo("id", serverId).findFirst();
        if(server != null) {
            txtName.setText(server.getName());
            localUrlText.setText(server.getLocalUrl());
            remoteUrlText.setText(server.getRemoteUrl());
            txtUsername.setText(server.getUsername());
            txtPassword.setText(server.getPassword());
        }
        realm.close();

        RxTextView.textChanges(remoteUrlText)
                .compose(bindToLifecycle())
                .subscribe(text -> {
                    errorRemoteUrlText.setVisibility(text.length() <= 0 || Patterns.WEB_URL.matcher(text).matches() ? View.GONE : View.VISIBLE);
                });

        RxTextView.textChanges(localUrlText)
                .compose(bindToLifecycle())
                .subscribe(text -> {
                    errorLocalUrlText.setVisibility(text.length() <= 0 || Patterns.WEB_URL.matcher(text).matches() ? View.GONE : View.VISIBLE);
                });
    }

    private String toUrl(String text){

        Uri uri = Uri.parse(text);
        return uri.toString();
    }

    @Override
    public void onPause() {
        super.onPause();

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            ServerDB server = new ServerDB();
            if(serverId <= 0) {
                server.setId(ServerDB.getUniqueId());
                serverId = server.getId();
            } else {
                server.setId(serverId);
            }
            server.setName(txtName.getText().toString());
            server.setLocalUrl(toUrl(localUrlText.getText().toString()));
            server.setRemoteUrl(toUrl(remoteUrlText.getText().toString()));
            server.setUsername(txtUsername.getText().toString());
            server.setPassword(txtPassword.getText().toString());
            realm1.copyToRealmOrUpdate(server);
        });
        realm.close();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(EXTRA_SERVER_ID, serverId);
        super.onSaveInstanceState(outState);
    }
}
