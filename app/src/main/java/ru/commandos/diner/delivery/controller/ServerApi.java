package ru.commandos.diner.delivery.controller;


import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.commandos.diner.delivery.model.Order;

public interface ServerApi {

    @GET("/delivery/check")
    Single<Order> getOrderWithID(@Query("courierUuid") String id);
}
