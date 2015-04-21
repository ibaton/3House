package treehou.se.habit.connector.requests;

import android.text.TextUtils;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
* Created by ibaton on 2014-09-10.
*/
public class AuthRequest extends StringRequest {

    private static final String TAG = "AuthRequest";

    private String username;
    private String password;

    public AuthRequest(int method, String url, String username, String password,
                       Response.Listener<String> responseListener, Response.ErrorListener errorListener){
        super(method, url, responseListener, errorListener);
        this.username = username;
        this.password = password;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {

        return createBasicAuthHeader(username, password);
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
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String stringResponse = "";
        /*if(response.data != null){
            stringResponse = new String(response.data, Charset.defaultCharset());
        }*/
        try {
            stringResponse = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            stringResponse = new String(response.data);
        }
        return Response.success(stringResponse, HttpHeaderParser.parseCacheHeaders(response));
    }
}
