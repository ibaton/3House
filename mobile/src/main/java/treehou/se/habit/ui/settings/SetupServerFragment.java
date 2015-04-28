package treehou.se.habit.ui.settings;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import treehou.se.habit.R;
import treehou.se.habit.core.Server;

public class SetupServerFragment extends Fragment {

    private static final String ARG_SERVER = "ARG_SERVER";

    private static final String EXTRA_SERVER_ID = "EXTRA_SERVER_ID";

    private EditText txtName;
    private EditText txtLocalUrl;
    private EditText txtRemoteUrl;
    private EditText txtUsername;
    private EditText txtPassword;

    private Server server;

    private Button btnBack;

    public static SetupServerFragment newInstance() {
        return new SetupServerFragment();
    }

    public static SetupServerFragment newInstance(Server server) {
        SetupServerFragment fragment = new SetupServerFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_SERVER, server.getId());
        fragment.setArguments(args);
        return fragment;
    }

    public SetupServerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null && savedInstanceState.containsKey(EXTRA_SERVER_ID)){
            long serverId = savedInstanceState.getLong(EXTRA_SERVER_ID);
            server = Server.load(Server.class, serverId);
        }else if (getArguments() != null) {
            long serverId = getArguments().getLong(ARG_SERVER);
            server = Server.load(Server.class, serverId);
        }else{
            server = new Server();
            server.save();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_setup_server, container, false);

        txtName = (EditText) rootView.findViewById(R.id.txt_server_name);
        txtLocalUrl = (EditText) rootView.findViewById(R.id.txt_server_local);
        txtRemoteUrl = (EditText) rootView.findViewById(R.id.txt_server_remote);
        txtUsername = (EditText) rootView.findViewById(R.id.txt_username);
        txtPassword = (EditText) rootView.findViewById(R.id.txt_password);
        btnBack = (Button) rootView.findViewById(R.id.btn_back);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        txtName.setText(server.getName());
        txtLocalUrl.setText(server.getLocalUrl());
        txtRemoteUrl.setText(server.getRemoteUrl());
        txtUsername.setText(server.getUsername());
        txtPassword.setText(server.getPassword());
    }

    private String toUrl(String text){

        Uri uri = Uri.parse(text);
        return uri.toString();
    }

    @Override
    public void onPause() {
        super.onPause();

        server.setName(txtName.getText().toString());
        server.setLocalUrl(toUrl(txtLocalUrl.getText().toString()));
        server.setRemoteUrl(toUrl(txtRemoteUrl.getText().toString()));
        server.setUsername(txtUsername.getText().toString());
        server.setPassword(txtPassword.getText().toString());
        server.save();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putLong(EXTRA_SERVER_ID, server.getId());

        super.onSaveInstanceState(outState);
    }
}
