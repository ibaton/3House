package se.treehou.ng.ohcommunicator.connector.models;

import android.content.Context;
import android.text.TextUtils;

public class OHServer {

    private String name;
    private String username;
    private String password;
    private String localurl;
    private String remoteurl;
    private int majorversion;

    public OHServer() {}

    public OHServer(String name, String username, String password, String localurl, String remoteurl, int majorversion) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.localurl = localurl;
        this.remoteurl = remoteurl;
        this.majorversion = majorversion;
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
        return remoteurl;
    }

    public void setRemoteurl(String remoteurl) {
        this.remoteurl = remoteurl;
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

    public static String getDisplayName(Context context, OHServer serverDB){
        return serverDB.getName();
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
        return (""+name+":"+localurl+":"+remoteurl).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return hashCode() == o.hashCode();
    }
}
