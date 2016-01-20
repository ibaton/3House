package se.treehou.ng.ohcommunicator.core;

import android.text.TextUtils;

public class OHServer {

    private String name = "";
    private String username = "";
    private String password = "";
    private String localUrl = "";
    private String remoteUrl = "";
    private int majorVersion = 0;

    public OHServer(){}

    public String getName() {
        return name;
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
        if(obj instanceof OHServer){
            OHServer server = (OHServer) obj;

            return ((getUsername() != null || this.getPassword().equals(server.getUsername())) &&
                    (getPassword() == null || getPassword().equals(server.getPassword())) &&
                    (getLocalUrl() == null || getLocalUrl().equals(server.getLocalUrl())) &&
                    (getRemoteUrl() == null || getRemoteUrl().equals(server.getRemoteUrl())));
        }

        return super.equals(obj);
    }
}
