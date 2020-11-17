package ru.commandos.diner.delivery.controller;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.commandos.diner.delivery.BuildConfig;

public class CourierService {

    private static CourierService courierService;
    private static final String BASE_URL = "https://10.0.2.2:8080";
    private Retrofit retrofit;

    private CourierService() {
        retrofit = new Retrofit.Builder().
                baseUrl(BASE_URL)
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
        if (BuildConfig.BUILD_TYPE.equals("debug")) {
            return new ServerMock();
        } else {
            return retrofit.create(ServerApi.class);
        }
    }
}
