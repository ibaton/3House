package se.treehou.ng.ohcommunicator.core;

import android.net.Uri;

import se.treehou.ng.ohcommunicator.core.db.OHSitemap;

public class OHSitemapWrapper {

    private OHSitemap sitemapDB;

    public OHSitemapWrapper() {}

    public OHSitemapWrapper(OHSitemap sitemapDB) {
        this.sitemapDB = sitemapDB;
    }

    public OHSitemap getDB() {
        return sitemapDB;
    }

    public void setDB(OHSitemap sitemapDB) {
        this.sitemapDB = sitemapDB;
    }

    public long getId(){
        return sitemapDB.getId();
    }

    private void setId(long id){
        sitemapDB.setId(id);
    }

    public String getName() {
        return sitemapDB.getName();
    }

    public void setName(String name) {
        sitemapDB.setName(name);
    }

    public String getLabel() {
        return sitemapDB.getLabel();
    }

    public void setLabel(String label) {
        sitemapDB.setLabel(label);
    }

    public String getLink() {
        return sitemapDB.getLink();
    }

    public void setLink(String link) {
        sitemapDB.setLink(link);
    }

    public OHServerWrapper getServer() {
        return OHServerWrapper.toOH(sitemapDB.getServer());
    }

    public void setServer(OHServerWrapper server) {
        sitemapDB.setServer(server.getDB());
    }

    @Override
    public String toString() {
        return getLabel();
    }

    @Override
    public boolean equals(Object o) {

        if(o instanceof OHSitemapWrapper){
            OHSitemapWrapper sitemap = (OHSitemapWrapper) o;

            return (getName().equals(sitemap.getName()) && getServer().equals(sitemap.getServer()));
        }

        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return getName().hashCode()+(getName().hashCode()+this.getServer().getName().hashCode());
    }

    public boolean isLocal(){
        OHServerWrapper server = getServer();
        Uri uri = Uri.parse(getLink());

        try{
            return uri.getHost().equals(Uri.parse(server.getLocalUrl()).getHost());
        }catch (Exception e){}

        return false;
    }

    public static OHSitemapWrapper load(long id){
        OHSitemap sitemapDB = OHSitemap.load(id);
        if(sitemapDB != null){
            return new OHSitemapWrapper(sitemapDB);
        }
        return null;
    }

    public void save(){
        OHSitemap.save(getDB());
    }

    public OHLinkedPageWrapper getHomepage(){
        return new OHLinkedPageWrapper(getDB().getHomepage());
    }
}
