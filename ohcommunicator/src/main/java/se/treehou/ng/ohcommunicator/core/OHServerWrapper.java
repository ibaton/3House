package se.treehou.ng.ohcommunicator.core;

import android.content.Context;
import android.text.TextUtils;

import java.util.List;

import se.treehou.ng.ohcommunicator.core.db.OHserver;

public class OHServerWrapper {

    private OHserver serverDB;

    public OHServerWrapper(){
        serverDB = new OHserver();
    }

    public OHServerWrapper(OHserver serverDB) {
        this.serverDB = serverDB;
    }

    public static OHServerWrapper toOH(OHserver serverDB){
        return new OHServerWrapper(serverDB);
    }

    public OHserver getDB() {
        return serverDB;
    }

    public void setDB(OHserver serverDB) {
        this.serverDB = serverDB;
    }

    public long getId(){
        return serverDB.getId();
    }

    public void setId(long id) {
        serverDB.setId(id);
    }

    public String getName() {
        return serverDB.getName();
    }

    public void setName(String name) {
        serverDB.setName(name);
    }

    public String getUsername() {
        return serverDB.getUsername();
    }

    public void setUsername(String username) {
        serverDB.setUsername(username);
    }

    public String getPassword() {
        return serverDB.getPassword();
    }

    public void setPassword(String password) {
        serverDB.setPassword(password);
    }

    public String getLocalUrl() {
        return serverDB.getLocalurl();
    }

    public void setLocalUrl(String localUrl) {
        serverDB.setLocalurl(localUrl);
    }

    public String getRemoteUrl() {
        return serverDB.getRemoteurl();
    }

    public void setRemoteUrl(String remoteUrl) {
        serverDB.setRemoteurl(remoteUrl);
    }

    public String getUrl(){
        return !TextUtils.isEmpty(getLocalUrl())?getLocalUrl():getRemoteUrl();
    }

    public boolean requiresAuth() {
        return !TextUtils.isEmpty(getUsername()) && !TextUtils.isEmpty(getPassword());
    }

    /**
     * Set the major version of server
     * @param majorVersion
     */
    public void setMajorVersion(int majorVersion){
        serverDB.setMajorversion(majorVersion);
    }

    public int getMajorVersion() {
        return serverDB.getMajorversion();
    }

    public boolean haveRemote(){
        return (getRemoteUrl()!=null && !getRemoteUrl().trim().equals(""));
    }

    public boolean haveLocal(){
        return (getLocalUrl()!=null && !getLocalUrl().trim().equals(""));
    }

    public static List<OHServerWrapper> loadAll(){
        /*List<OHserver> dbServers = OHRealm.realm().allObjects(OHserver.class);
        List<OHServerWrapper> servers = new ArrayList<>();
        for(OHserver db : dbServers){
            servers.add(new OHServerWrapper(db));
        }
        return servers;*/
        return null;
    }

    public static OHServerWrapper load(long id){
        OHserver serverDB = OHserver.load(id);
        if(serverDB != null){
            return new OHServerWrapper(serverDB);
        }

        return null;
    }

    public void save(){
        OHserver.save(serverDB);
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof OHServerWrapper){
            OHServerWrapper server = (OHServerWrapper) obj;

            return ((getUsername() != null || this.getPassword().equals(server.getUsername())) &&
                    (getPassword() == null || getPassword().equals(server.getPassword())) &&
                    (getLocalUrl() == null || getLocalUrl().equals(server.getLocalUrl())) &&
                    (getRemoteUrl() == null || getRemoteUrl().equals(server.getRemoteUrl())));
        }

        return super.equals(obj);
    }

    public String getDisplayName(Context context){
        return getName();
    }
}
