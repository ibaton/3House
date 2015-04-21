package treehou.se.habit.connector;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;
import treehou.se.habit.core.Item;
import treehou.se.habit.core.Sitemap;

/**
* Created by ibaton on 2015-03-01.
*/
public interface OpenHabService {

    @Headers("Accept: application/json")
    @GET("/rest/sitemaps")
    void listSitemaps(retrofit.Callback<Sitemap.SitemapHolder> callback);

    @Headers("Accept: application/json")
    @GET("/rest/sitemaps/{id}")
    void getSitemap(@Path("id") String id, retrofit.Callback<Sitemap> callback);

    @Headers("Accept: application/json")
    @GET("/rest/items/")
    void getItems(retrofit.Callback<Item.ItemHolder> callback);

    @Headers("Accept: application/json")
    @GET("/rest/items/{id}")
    void getItem(@Path("id") String id, retrofit.Callback<Item> callback);

    @Headers({
        "Accept: application/text",
        "Content-Type: text/plain"
    })
    @POST("/rest/items/{id}")
    void sendCommand(@Body String command, @Path("id") String id, retrofit.Callback<Response> callback);

    @Headers({
            "Accept: application/text",
            "Content-Type: text/plain"
    })
    @POST("/rest/items/{id}")
    Response sendCommand(@Body String command, @Path("id") String id);
}
