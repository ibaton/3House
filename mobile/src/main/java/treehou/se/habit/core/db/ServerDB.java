package treehou.se.habit.core.db;

import android.content.Context;
import android.text.TextUtils;

import java.util.List;

import io.realm.RealmObject;
import se.treehou.ng.ohcommunicator.core.OHServer;
import treehou.se.habit.R;

public class ServerDB extends RealmObject {

    private String name = "";

    @Column(name = "username")
    private String username = "";

    @Column(name = "password")
    private String password = "";

    @Column(name = "localUrl")
    private String localUrl = "";

    @Column(name = "remoteUrl")
    private String remoteUrl = "";

    @Column(name = "majorVersion")
    private int majorVersion = 0;

    public ServerDB(){}

    public static ServerDB createFrom(OHServer server){
        ServerDB serverDB = new ServerDB();
        serverDB.setLocalUrl(server.getLocalUrl());
        serverDB.setRemoteUrl(server.getRemoteUrl());
        serverDB.setName(server.getName());
        serverDB.setMajorVersion(server.getMajorVersion());
        serverDB.setPassword(server.getPassword());
        serverDB.setUsername(server.getUsername());

        return serverDB;
    }

    public static OHServer toGeneric(ServerDB server){
        OHServer ohServer = new OHServer();
        ohServer.setLocalUrl(server.getLocalUrl());
        ohServer.setRemoteUrl(server.getRemoteUrl());
        ohServer.setName(server.getName());
        ohServer.setMajorVersion(server.getMajorVersion());
        ohServer.setPassword(server.getPassword());
        ohServer.setUsername(server.getUsername());

        return ohServer;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName(Context context){
        return TextUtils.isEmpty(name) ? context.getString(R.string.home) : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLocalUrl() {
        return localUrl;
    }

    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }

    public String getRemoteUrl() {
        return remoteUrl;
    }

    public void setRemoteUrl(String remoteUrl) {
        this.remoteUrl = remoteUrl;
    }

    public String getUrl(){
        return !TextUtils.isEmpty(localUrl)?localUrl:remoteUrl;
    }

    public boolean requiresAuth() {
        return !TextUtils.isEmpty(username) && !TextUtils.isEmpty(password);
    }

    /**
     * Set the major version of server
     * @param majorVersion
     */
    public void setMajorVersion(int majorVersion){
        this.majorVersion = majorVersion;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public static List<ServerDB> getServers(){
        return new Select()
                .from(ServerDB.class)
                .execute();
    }

    public boolean haveRemote(){
        return (getRemoteUrl()!=null && !getRemoteUrl().trim().equals(""));
    }

    public boolean haveLocal(){
        return (getLocalUrl()!=null && !getLocalUrl().trim().equals(""));
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ServerDB){
            ServerDB server = (ServerDB) obj;

            return ((getUsername() != null || this.getPassword().equals(server.getUsername())) &&
                    (getPassword() == null || getPassword().equals(server.getPassword())) &&
                    (getLocalUrl() == null || getLocalUrl().equals(server.getLocalUrl())) &&
                    (getRemoteUrl() == null || getRemoteUrl().equals(server.getRemoteUrl())));
        }

        return super.equals(obj);
    }
}
