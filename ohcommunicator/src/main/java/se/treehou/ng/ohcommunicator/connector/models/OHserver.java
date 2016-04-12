package se.treehou.ng.ohcommunicator.connector.models;

import android.text.TextUtils;

public class OHServer {

    private long id;
    private String name;
    private String username;
    private String password;
    private String localurl;
    private String remoteUrl;
    private int majorversion;

    public OHServer() {}

    public OHServer(long id, String name, String username, String password, String localurl, String remoteUrl, int majorversion) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.localurl = localurl;
        this.remoteUrl = remoteUrl;
        this.majorversion = majorversion;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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
        return localurl;
    }

    public void setLocalurl(String localurl) {
        this.localurl = localurl;
    }

    public String getRemoteUrl() {
        return remoteUrl;
    }

    public void setRemoteUrl(String remoteUrl) {
        this.remoteUrl = remoteUrl;
    }

    /**
     * Set the major version of server
     * @param majorversion
     */
    public void setMajorversion(int majorversion){
        this.majorversion = majorversion;
    }

    public int getMajorversion() {
        return majorversion;
    }

    public String getDisplayName(){
        return getName();
    }

    public boolean requiresAuth() {
        return TextUtils.isEmpty(username) && TextUtils.isEmpty(password);
    }

    /**
     * Check if server has a local url.
     * @return true if there is a local url registered. Else false.
     */
    public boolean haveLocal() {
        return !TextUtils.isEmpty(getLocalUrl());
    }

    /**
     * Check if server has a remote url.
     * @return true if there is a remote url registered. Else false.
     */
    public boolean haveRemote() {
        return !TextUtils.isEmpty(getRemoteUrl());
    }

    @Override
    public int hashCode() {
        return (""+name+":"+localurl+":"+ remoteUrl).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return hashCode() == o.hashCode();
    }
}
