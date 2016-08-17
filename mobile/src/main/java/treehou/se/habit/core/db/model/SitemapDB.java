package treehou.se.habit.core.db.model;

import android.net.Uri;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SitemapDB extends RealmObject {

    @PrimaryKey
    private long id;

    private String name;
    private String label;
    private String link;
    private ServerDB server;
    private LinkedPageDB homepage;

    private SitemapSettingsDB settingsDB;

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

    public LinkedPageDB getHomepage() {
        return homepage;
    }

    public void setHomepage(LinkedPageDB homepage) {
        this.homepage = homepage;
    }

    public ServerDB getServer() {
        return server;
    }

    public void setServer(ServerDB server) {
        this.server = server;
    }

    public static boolean isLocal(SitemapDB sitemap){
        Uri uri = Uri.parse(sitemap.getLink());

        try{
            return uri.getHost().equals(Uri.parse(sitemap.getServer().getLocalUrl()).getHost());
        }catch (Exception e){}

        return false;
    }

    public SitemapSettingsDB getSettingsDB() {
        return settingsDB;
    }

    public void setSettingsDB(SitemapSettingsDB settingsDB) {
        this.settingsDB = settingsDB;
    }

    /**
     * Generate a unique id for realm object
     * @param realm
     * @return
     */
    public static long getUniqueId(Realm realm) {
        long id = 1;
        Number num = realm.where(SitemapDB.class).max("id");
        if (num != null) id = num.longValue() + 1;

        return id;
    }
}
