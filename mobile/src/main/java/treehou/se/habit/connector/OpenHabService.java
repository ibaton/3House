package treehou.se.habit.connector;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Path;
import se.treehou.ng.ohcommunicator.core.OHLinkedPageWrapper;
import se.treehou.ng.ohcommunicator.core.OHSitemapWrapper;
public interface OpenHabService {

    @Headers("Accept: application/json")
    @GET("/rest/sitemaps")
    void listSitemaps(retrofit.Callback<List<OHSitemapWrapper>> callback);

    @Headers("Accept: application/json")
    @GET("/rest/sitemaps/{id}")
    void getSitemap(@Path("id") String id, retrofit.Callback<OHSitemapWrapper> callback);

    @Headers("Accept: application/json")
    @GET("/")
    void getPage(retrofit.Callback<OHLinkedPageWrapper> callback);
}
