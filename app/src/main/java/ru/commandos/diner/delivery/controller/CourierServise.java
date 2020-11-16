package ru.commandos.diner.delivery.controller;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CourierServise {

    private static CourierServise courierServise;
    private static final String BASE_URL = "http://10.0.2.2";
    private Retrofit retrofit;

    private CourierServise() {
        retrofit = new Retrofit.Builder().
                baseUrl(BASE_URL).
                addConverterFactory(GsonConverterFactory.create()).
                build();
    }

    public static CourierServise getInstance() {
        if(courierServise == null) {
            courierServise = new CourierServise();
        }
        return courierServise;
    }

    public JSONPlaceHolderApi getJSONApi() {
        return retrofit.create(JSONPlaceHolderApi.class);
    }
}
