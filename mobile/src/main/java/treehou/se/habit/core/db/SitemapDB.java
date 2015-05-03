package treehou.se.habit.core.db;

import android.net.Uri;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import treehou.se.habit.core.Server;
import treehou.se.habit.core.Sitemap;

/**
 * Created by ibaton on 2014-09-10.
 */
@Table(name = "Sitemap")
public class SitemapDB extends Model {

    @Column(name = "name")
    private String name;

    @Column(name = "label")
    private String label;

    @Column(name = "link", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private String link;

    @Column(name = "server")
    private Server server;

    public SitemapDB() {}

    public SitemapDB(Sitemap sitemap) {
        name = sitemap.getName();
        label = sitemap.getLabel();
        link = sitemap.getLink();
        server = sitemap.getServer();
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

    public boolean isLocal(){
        Server server = getServer();
        Uri uri = Uri.parse(link);

        try{
            return uri.getHost().equals(Uri.parse(server.getLocalUrl()).getHost());
        }catch (Exception e){}

        return false;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
        save();
    }

    @Override
    public String toString() {
        return label;
    }

    @Override
    public boolean equals(Object o) {

        if(o instanceof SitemapDB){
            SitemapDB sitemap = (SitemapDB) o;

            return (this.name.equals(sitemap.name) && this.server.getId().equals(sitemap.getServer().getId()));
        }

        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return name.hashCode()+(name.hashCode()+this.getServer().getName().hashCode());
    }
}
