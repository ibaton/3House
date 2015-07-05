package treehou.se.habit.core;

import android.net.Uri;

import treehou.se.habit.core.db.ServerDB;

public class Sitemap {
    private String name;
    private String label;
    private String link;
    private LinkedPage homepage;
    private long server;

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

    public boolean isLocal(){
        ServerDB server = getServer();
        Uri uri = Uri.parse(link);

        try{
            return uri.getHost().equals(Uri.parse(server.getLocalUrl()).getHost());
        }catch (Exception e){}

        return false;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public LinkedPage getHomepage() {
        return homepage;
    }

    public void setHomepage(LinkedPage homepage) {
        this.homepage = homepage;
    }

    public long getServerId() {
        return server;
    }

    public void setServer(ServerDB server) {
        this.server = server.getId();
    }

    public void setServerId(long server) {
        this.server = server;
    }

    public ServerDB getServer(){
        return ServerDB.load(ServerDB.class, server);
    }

    @Override
    public String toString() {
        return label;
    }

    @Override
    public boolean equals(Object o) {

        if(o instanceof Sitemap){
            Sitemap sitemap = (Sitemap) o;

            return (this.name.equals(sitemap.name) && this.getServerId()==sitemap.getServerId());
        }

        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return name.hashCode()+(name.hashCode()+this.getServer().getName().hashCode());
    }
}
