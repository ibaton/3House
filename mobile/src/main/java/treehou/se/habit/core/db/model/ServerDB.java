package treehou.se.habit.core.db.model;

import android.text.TextUtils;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;

public class ServerDB extends RealmObject {

    @PrimaryKey
    private long id = 0;

    private String name = "";
    private String username = "";
    private String password = "";
    private String localurl = "";
    private String remoteurl = "";
    private int majorversion;

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
        if(name == null) name = "";
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if(username == null) username = "";
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if(password == null) password = "";
        this.password = password;
    }

    public String getLocalUrl() {
        return localurl;
    }

    public void setLocalUrl(String localurl) {
        if(localurl == null) localurl = "";
        this.localurl = localurl;
    }

    public String getRemoteUrl() {
        return remoteurl;
    }

    public void setRemoteUrl(String remoteurl) {
        if(remoteurl == null) remoteurl = "";
        this.remoteurl = remoteurl;
    }

    /**
     * Set the major version of server
     *
     * @param majorversion
     */
    public void setMajorversion(int majorversion) {
        this.majorversion = majorversion;
    }

    public int getMajorversion() {
        return majorversion;
    }

    public boolean requiresAuth() {
        return !TextUtils.isEmpty(username) && !TextUtils.isEmpty(password);
    }

    public static ServerDB load(Realm realm, long id) {
        return realm.where(ServerDB.class).equalTo("id", id).findFirst();
    }

    public static void save(ServerDB item) {

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        if (item.getId() <= 0) {
            item.setId(getUniqueId());
        }

        realm.copyToRealmOrUpdate(item);
        realm.commitTransaction();
        realm.close();
    }

    public String getDisplayName() {
        return getName();
    }

    public static long getUniqueId() {
        Realm realm = Realm.getDefaultInstance();
        long id = 1;
        Number num = realm.where(ServerDB.class).max("id");
        if (num != null) id = num.longValue() + 1;
        realm.close();

        return id;
    }

    /**
     * Convert this object to a generic object that can be handled by openhab lib
     * @return generic server compatable with openhab lib.
     */
    public OHServer toGeneric() {
        return new OHServer(
            getId(),
            getName(),
            getUsername(),
            getPassword(),
            getLocalUrl(),
            getRemoteUrl(),
            getMajorversion());
    }

    /**
     * Convert this object to a db object that can be stored in db.
     * @return database server object.
     */
    public static ServerDB fromGeneric(OHServer server) {
        ServerDB serverDB = new ServerDB();
        serverDB.setId(ServerDB.getUniqueId());
        serverDB.setName(server.getName());
        serverDB.setLocalUrl(server.getLocalUrl());
        serverDB.setRemoteUrl(server.getRemoteUrl());
        serverDB.setUsername(server.getUsername());
        serverDB.setPassword(server.getPassword());
        return serverDB;
    }
}
