package se.treehou.ng.ohcommunicator.core;

public class OHSitemap {

    private String name;
    private String label;
    private String link;
    private OHServer server;

    public OHSitemap() {}

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

    public OHServer getServer() {
        return server;
    }

    public void setServer(OHServer server) {
        this.server = server;
    }

    @Override
    public String toString() {
        return label;
    }

    @Override
    public boolean equals(Object o) {

        if(o instanceof OHSitemap){
            OHSitemap sitemap = (OHSitemap) o;

            return (this.name.equals(sitemap.name) && this.server.equals(sitemap.getServer()));
        }

        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return name.hashCode()+(name.hashCode()+this.getServer().getName().hashCode());
    }
}
