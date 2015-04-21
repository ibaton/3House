package treehou.se.habit.connector.requests;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import treehou.se.habit.core.Util;

/**
* Created by ibaton on 2014-09-10.
*/
public class GsonRequest<T> extends Request<T> {

    public static final String TAG = "AuthRequest";

    private String username;
    private String password;

    private final Class<T> clazz;
    private final Response.Listener<T> listener;
    private Response.ErrorListener errorListener;

    private Gson gson = Util.createGsonBuilder();

    public GsonRequest(int method, String url, String username, String password,
                       Class<T> clazz, Response.Listener<T> listener,
                       Response.ErrorListener errorListener){

        super(method, url, errorListener);

        this.username = username;
        this.password = password;
        this.clazz = clazz;
        this.listener = listener;
        this.errorListener = errorListener;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> authHeaders = createBasicAuthHeader(username, password);
        authHeaders.put("Accept", "application/json");
        return authHeaders;
    }

    public Response.Listener<T> getResponseListener(){
        return listener;
    }

    public Response.ErrorListener getErrorListener(){
        return errorListener;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    private Map<String, String> createBasicAuthHeader(String username, String password) {
        Map<String, String> headerMap = new HashMap<>();

        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            String credentials = String.format("%s:%s", username, password);
            String encodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT);
            headerMap.put("Authorization", "Basic " + encodedCredentials);
        }

        return headerMap;
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data,
                    Charset.defaultCharset());
            Log.d(TAG,json);
            return Response.success(
                    gson.fromJson(json, clazz),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (JsonSyntaxException e) {
            Log.d(TAG, "GsonRequest error " + e);
            return Response.error(new ParseError(e));
        }
    }
}
