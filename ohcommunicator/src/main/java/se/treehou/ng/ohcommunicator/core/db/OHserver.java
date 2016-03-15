package se.treehou.ng.ohcommunicator.core.db;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmObject;

public class OHserver /*extends RealmObject*/ {

    //@PrimaryKey
    private long id = 0;

    private String name;
    private String username;
    private String password;
    private String localurl;
    private String remoteurl;
    private int majorversion;
    //private RealmList<OHSitemap> sitemaps = new RealmList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /*public RealmList<OHSitemap> getSitemaps() {
        return sitemaps;
    }

    public void setSitemaps(RealmList<OHSitemap> sitemaps) {
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

    public String getLocalurl() {
        return localurl;
    }

    public void setLocalurl(String localurl) {
        this.localurl = localurl;
    }

    public String getRemoteurl() {
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

    public static OHserver load(long id){
        return null;
        //return OHRealm.realm().where(OHserver.class).equalTo("id", id).findFirst();
    }

    public static void save(OHserver item){

        Realm realm = OHRealm.realm();
        realm.beginTransaction();
        if(item.getId() <= 0) {
            item.setId(getUniqueId());
        }

        /*realm.copyToRealmOrUpdate(item);
        realm.commitTransaction();
        realm.close();*/
    }

    public static String getDisplayName(Context context, OHserver serverDB){
        return serverDB.getName();
    }

    public static long getUniqueId() {
        /*Realm realm = OHRealm.realm();
        long id = 1;
        Number num = realm.where(OHserver.class).max("id");
        if (num != null) id = num.longValue() + 1;
        realm.close();

        return id;*/
        return 0;
    }
}
