package se.treehou.ng.ohcommunicator.connector;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class BasicAuthServiceGenerator {

    private static final String TAG = BasicAuthServiceGenerator.class.getSimpleName();

    // No need to instantiate this class.
    private BasicAuthServiceGenerator() {}

    public static <S> S createService(Class<S> serviceClass, final String usernarname, final String password, String url) {

        OkHttpClient.Builder client = new OkHttpClient.Builder();

        if(!TextUtils.isEmpty(usernarname) && !TextUtils.isEmpty(password)) {
            client.authenticator(new Authenticator() {
                @Override
                public Request authenticate(Route route, Response response) throws IOException {
                    String credential = Credentials.basic(usernarname, password);
                    return response.request().newBuilder().header("Authorization", credential).build();
                }
            });
        }

        try {
            client.sslSocketFactory(TrustModifier.createFactory());
            client.hostnameVerifier(new TrustModifier.NullHostNameVerifier());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }

        if(!ConnectorUtil.isValidServerUrl(url)){
            url = "http://127.0.0.1:8080";
        }

        Retrofit retrofit;
        try {
            Retrofit.Builder builder = new Retrofit.Builder()
                    .client(client.build())
                    .baseUrl(url)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(GsonHelper.createGsonBuilder()))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
            retrofit = builder.build();
        } catch (Exception e) {
            Log.e(TAG, "Failed to generate service", e);
            throw e;
        }

        return retrofit.create(serviceClass);
    }
}
