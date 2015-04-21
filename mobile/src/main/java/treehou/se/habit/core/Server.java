package treehou.se.habit.core;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by ibaton on 2014-09-10.
 */
@Table(name = "Server")
public class Server extends Model {

    @Column(name = "name")
    private String name = "";

    @Column(name = "username")
    private String username = "";

    @Column(name = "password")
    private String password = "";

    @Column(name = "localUrl")
    private String localUrl = "";

    @Column(name = "remoteUrl")
    private String remoteUrl = "";

    public Server(){}

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
        return localUrl;
    }

    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }

    public String getRemoteUrl() {
        return remoteUrl;
    }

    public void setRemoteUrl(String remoteUrl) {
        this.remoteUrl = remoteUrl;
    }

    public String getUrl(){
        return localUrl!=null?localUrl:remoteUrl;
    }

    public static List<Server> getServers(){
        return new Select()
                .from(Server.class)
                .execute();
    }

    public boolean haveRemote(){
        return (getRemoteUrl()!=null && !getRemoteUrl().trim().equals(""));
    }

    public boolean haveLocal(){
        return (getLocalUrl()!=null && !getLocalUrl().trim().equals(""));
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Server){
            Server server = (Server) obj;

            return ((getUsername() != null || this.getPassword().equals(server.getUsername())) &&
                    (getPassword() == null || getPassword().equals(server.getPassword())) &&
                    (getLocalUrl() == null || getLocalUrl().equals(server.getLocalUrl())) &&
                    (getRemoteUrl() == null || getRemoteUrl().equals(server.getRemoteUrl())));
        }

        return super.equals(obj);
    }
}
