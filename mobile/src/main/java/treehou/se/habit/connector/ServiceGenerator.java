package treehou.se.habit.connector;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;
import treehou.se.habit.core.Util;

/**
 * Created by ibaton on 2015-02-28.
 */
public class ServiceGenerator {

    private static SSLSocketFactory factory;

    // No need to instantiate this class.
    private ServiceGenerator() {
    }

    public static <S> S createService(Class<S> serviceClass, String baseUrl, String username, String password) {

        OkHttpClient client = new OkHttpClient();
        try {
            client.setSslSocketFactory(TrustModifier.createFactory());
            client.setHostnameVerifier(new TrustModifier.NullHostNameVerifier());
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            e.printStackTrace();
        }

        // set endpoint url and use OkHTTP as HTTP client
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(baseUrl)
                .setClient(new OkClient(client));

        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            // concatenate username and password with colon for authentication
            final String credentials = username + ":" + password;

            builder.setRequestInterceptor(new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {

                // create Base64 encodet string
                String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT);
                request.addHeader("Authorization", auth);
                }
            });
        }
        builder.setConverter(new OpenHabConverter());
        RestAdapter adapter = builder.build();

        return adapter.create(serviceClass);
    }

    public static class OpenHabConverter implements Converter{

        private static final String TAG = "OpenHabConverter";

        private GsonConverter gsonConverter = new GsonConverter(Util.createGsonBuilder());

        public OpenHabConverter() {}

        @Override
        public Object fromBody(TypedInput body, Type type) throws ConversionException {

            Log.d(TAG, body.mimeType());
            if(body.mimeType().equals("text/plain") && type == String.class){
                try {
                    String sBody = IOUtils.toString(body.in());
                    return sBody;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return gsonConverter.fromBody(body, type);
        }

        @Override
        public TypedOutput toBody(Object object) {

            if(object instanceof String){
                return new TextTypedOutput(((String) object).getBytes());
            }

            return gsonConverter.toBody(object);
        }
    }

    private static class TextTypedOutput implements TypedOutput {
        private final byte[] body;
        private final String mimeType;

        TextTypedOutput(byte[] bytes) {
            this.body = bytes;
            this.mimeType = "application/text; charset=" + "UTF-8";
        }

        @Override public String fileName() {
            return null;
        }

        @Override public String mimeType() {
            return mimeType;
        }

        @Override public long length() {
            return body.length;
        }

        @Override public void writeTo(OutputStream out) throws IOException {
            out.write(body);
        }
    }
}
