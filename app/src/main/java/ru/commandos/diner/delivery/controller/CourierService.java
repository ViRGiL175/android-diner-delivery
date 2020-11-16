package ru.commandos.diner.delivery.controller;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CourierService {

    private static CourierService courierService;
    private static final String BASE_URL = "http://10.0.2.2:8080";
    private Retrofit retrofit;

    private CourierService() {
        retrofit = new Retrofit.Builder().
                baseUrl(BASE_URL).
                addConverterFactory(GsonConverterFactory.create()).
                build();
    }

    public static CourierService getInstance() {
        if(courierService == null) {
            courierService = new CourierService();
        }
        return courierService;
    }

    public JSONPlaceHolderApi getJSONApi() {
        return retrofit.create(JSONPlaceHolderApi.class);
    }
}
