package ru.commandos.diner.delivery.controller;

import androidx.annotation.Nullable;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.internal.EverythingIsNonNull;
import ru.commandos.diner.delivery.BuildConfig;

@EverythingIsNonNull
public class CourierService {

    private static final String BASE_URL = "https://10.0.2.2:8080";
    @Nullable
    private static CourierService courierService;
    private final Retrofit retrofit;

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

    public ServerApi getServerApi() {
        if (BuildConfig.BUILD_TYPE.equals("debug")) {
            return new ServerMock();
        } else {
            return retrofit.create(ServerApi.class);
        }
    }
}
