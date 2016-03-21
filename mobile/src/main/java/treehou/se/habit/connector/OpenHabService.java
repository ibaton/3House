package treehou.se.habit.connector;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Path;
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap;

public interface OpenHabService {

    @Headers("Accept: application/json")
    @GET("/rest/sitemaps")
    void listSitemaps(retrofit.Callback<List<OHSitemap>> callback);

    @Headers("Accept: application/json")
    @GET("/rest/sitemaps/{id}")
    void getSitemap(@Path("id") String id, retrofit.Callback<OHSitemap> callback);

    @Headers("Accept: application/json")
    @GET("/")
    void getPage(retrofit.Callback<OHLinkedPage> callback);
}
