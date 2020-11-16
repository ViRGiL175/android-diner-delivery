package ru.commandos.diner.delivery.controller;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.commandos.diner.delivery.model.Order;

public interface JSONPlaceHolderApi {
    @GET("/delivery/check")
    public Call<Order> getOrderWithID(@Query("courierUuid") String id);
}
