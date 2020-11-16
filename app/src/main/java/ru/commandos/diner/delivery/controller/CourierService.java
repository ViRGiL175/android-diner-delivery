package ru.commandos.diner.delivery.controller;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class CourierService {

    private static final String BASE_URL = "https://10.0.2.2:8080";
    //    private static final String BASE_URL = "https://ya.ru";
    private static CourierService courierService;
    private final Retrofit retrofit;

    private CourierService() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = UnsafeOkHttpClient.getUnsafeOkHttpClientBuilder()
                .addInterceptor(interceptor)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static CourierService getInstance() {
        if (courierService == null) {
            courierService = new CourierService();
        }
        return courierService;
    }

    public ServerApi getJSONApi() {
        return retrofit.create(ServerApi.class);
    }
}
