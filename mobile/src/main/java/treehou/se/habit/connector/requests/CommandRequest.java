package treehou.se.habit.connector.requests;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;

import java.util.Map;

/**
 * Created by ibaton on 2014-10-05.
 */
public class CommandRequest extends AuthRequest {

    private String mBody;
    private Response.Listener<String> listener;
    private Response.ErrorListener errorListener;

    public CommandRequest(int method, String body, String url, String username, String password,
                          Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        super(method, url, username, password, responseListener, errorListener);

        this.listener = responseListener;
        this.errorListener = errorListener;
        mBody = body;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return mBody.getBytes();
    }

    public Response.Listener<String> getResponseListener(){
        return listener;
    }

    public Response.ErrorListener getErrorListener(){
        return errorListener;
    }

    public String getBodyString(){
        return mBody;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> authHeaders = super.getHeaders();
        authHeaders.put("Accept", "text/plain");
        authHeaders.put("Content-Type", "text/plain");
        return authHeaders;
    }
}
