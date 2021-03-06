package ru.commandos.diner.delivery.controller;

import androidx.annotation.Nullable;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.internal.EverythingIsNonNull;
import ru.commandos.diner.delivery.BuildConfig;

@EverythingIsNonNull
public class HttpService {

    private static final String BASE_URL = "http://10.0.2.2:8080";
    @Nullable
    private static HttpService httpService;
    private final Retrofit retrofit;

    private HttpService() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = UnsafeOkHttpClient.getUnsafeOkHttpClientBuilder()
                .addInterceptor(interceptor)
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create())
                .build();
    }

    public static HttpService getInstance() {
        if (httpService == null) {
            httpService = new HttpService();
        }
        return httpService;
    }

    public ServerApi getServerApi() {
        if (BuildConfig.BUILD_TYPE.equals("imitator")) {
            return new ServerMock();
        } else {
            return retrofit.create(ServerApi.class);
        }
    }
}
