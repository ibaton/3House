package se.treehou.ng.ohcommunicator.connector;

import android.text.TextUtils;

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

    // No need to instantiate this class.
    private BasicAuthServiceGenerator() {}

    public static <S> S createService(Class<S> serviceClass, final String usernarname, final String password, final String url) {

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
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            e.printStackTrace();
        }

        Retrofit.Builder builder = new Retrofit.Builder()
                .client(client.build())
                .baseUrl(url)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(GsonHelper.createGsonBuilder()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create());

        Retrofit retrofit = builder.build();

        return retrofit.create(serviceClass);
    }
}
