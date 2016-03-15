package treehou.se.habit.core.db.model;

import android.net.Uri;

public class OHSitemap /*extends RealmObject*/ {

    //@PrimaryKey
    private long id;

    private String name;
    private String label;
    private String link;
    private OHserver server;

    //@Ignore
    private OHLinkedPage homepage;

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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public OHLinkedPage getHomepage() {
        return homepage;
    }

    public void setHomepage(OHLinkedPage homepage) {
        this.homepage = homepage;
    }

    public OHserver getServer() {
        return server;
    }

    public void setServer(OHserver server) {
        this.server = server;
    }

    public static OHSitemap load(long id){
        //return OHRealm.realm().where(OHSitemap.class).equalTo("id", id).findFirst();
        return null;
    }

    public static void save(OHSitemap item){
        /*Realm realm = OHRealm.realm();
        realm.beginTransaction();
        if(item.getId() <= 0) {
            item.setId(getUniqueId());
        }

        realm.copyToRealmOrUpdate(item);
        realm.commitTransaction();
        realm.close();*/
    }

    public static long getUniqueId() {
        /*Realm realm = OHRealm.realm();
        long id = 1;
        Number num = realm.where(OHSitemap.class).max("id");
        if (num != null) id = num.longValue() + 1;
        realm.close();

        return id;*/
        return 0;
    }

    public static boolean isLocal(OHSitemap sitemap){
        Uri uri = Uri.parse(sitemap.getLink());

        try{
            return uri.getHost().equals(Uri.parse(sitemap.getServer().getLocalurl()).getHost());
        }catch (Exception e){}

        return false;
    }
}
