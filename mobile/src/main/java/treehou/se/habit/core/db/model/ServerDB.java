package treehou.se.habit.core.db.model;

import android.text.TextUtils;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;

public class ServerDB extends RealmObject {

    @PrimaryKey
    private long id = 0;

    private String name;
    private String username;
    private String password;
    private String localurl;
    private String remoteurl;
    private int majorversion;
    //private RealmList<SitemapDB> sitemaps = new RealmList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /*public RealmList<SitemapDB> getSitemaps() {
        return sitemaps;
    }

    public void setSitemaps(RealmList<SitemapDB> sitemaps) {
        this.sitemaps = sitemaps;
    }*/

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

    public void setLocalUrl(String localurl) {
        this.localurl = localurl;
    }

    public String getRemoteUrl() {
        return remoteurl;
    }

    public void setRemoteUrl(String remoteurl) {
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
        return TextUtils.isEmpty(username) && TextUtils.isEmpty(password);
    }

    public static ServerDB load(Realm realm, long id) {
        return realm.where(ServerDB.class).equalTo("id", id).findFirst();
    }

    public static void save(ServerDB item) {

        Realm realm = OHRealm.realm();
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
        Realm realm = OHRealm.realm();
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
}
