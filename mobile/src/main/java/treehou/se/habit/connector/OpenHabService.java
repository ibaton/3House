package treehou.se.habit.connector;

import java.util.List;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;
import treehou.se.habit.connector.models.Binding;
import treehou.se.habit.core.LinkedPage;
import treehou.se.habit.core.db.ItemDB;
import treehou.se.habit.core.Sitemap;

public interface OpenHabService {

    @Headers("Accept: application/json")
    @GET("/rest/sitemaps")
    void listSitemaps(retrofit.Callback<List<Sitemap>> callback);

    @Headers("Accept: application/json")
    @GET("/rest/sitemaps/{id}")
    void getSitemap(@Path("id") String id, retrofit.Callback<Sitemap> callback);

    @Headers("Accept: application/json")
    @GET("/")
    void getPage(retrofit.Callback<LinkedPage> callback);
}
