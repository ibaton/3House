package se.treehou.ng.ohcommunicator.connector;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import se.treehou.ng.ohcommunicator.core.OHBinding;
import se.treehou.ng.ohcommunicator.core.OHInboxItem;
import se.treehou.ng.ohcommunicator.core.OHItem;

public interface OpenHabService {

    @Headers("Accept: application/json")
    @GET("/rest/bindings")
    Call<List<OHBinding>> listBindings();

    @Headers("Accept: application/json")
    @GET("/rest/inbox")
    Call<List<OHInboxItem>> listInboxItems();

    @Headers("Accept: application/json")
    @POST("/rest/inbox/{thingUID}/ignore")
    Call<Void> ignoreInboxItems(@Path("thingUID") String thingUID);

    @Headers("Accept: application/json")
    @POST("/rest/inbox/{thingUID}/unignore")
    Call<Void> unignoreInboxItems(@Path("thingUID") String thingUID);

    @Headers("Accept: application/json")
    @POST("/rest/inbox/{thingUID}/approve")
    Call<Void> approveInboxItems(@Path("thingUID") String thingUID);

    @Headers("Accept: application/json")
    @GET("/rest/items/{id}")
    Call<OHItem> getItem(@Path("id") String id);

    @Headers("Accept: application/json")
    @GET("/rest/items/")
    Call<List<OHItem>> listItems();

    @Headers({
            "Accept: application/text",
            "Content-Type: text/plain"
    })
    @POST("/rest/items/{id}")
    Call<Void> sendCommand(@Body String command, @Path("id") String id);
}
