package se.treehou.ng.ohcommunicator.connector.models;

import android.net.Uri;

public class OHSitemap {

    private String name;
    private String label;
    private String link;
    private OHServer server;
    private OHLinkedPage homepage;

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

    public OHServer getServer() {
        return server;
    }

    public void setServer(OHServer server) {
        this.server = server;
    }

    public static boolean isLocal(OHSitemap sitemap){
        Uri uri = Uri.parse(sitemap.getLink());

        try{
            return uri.getHost().equals(Uri.parse(sitemap.getServer().getLocalUrl()).getHost());
        }catch (Exception e){}

        return false;
    }

    /**
     * Get name used in lists etc.
     * @return display name for sitemap.
     */
    public String getDisplayName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        return hashCode() == o.hashCode();
    }

    @Override
    public int hashCode() {
        return (""+name+":"+getServer().getName()).hashCode();
    }
}
