package ru.commandos.diner.delivery.controller;


import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.commandos.diner.delivery.model.Order;

public interface JSONPlaceHolderApi {

    @GET("/delivery/check")
    Observable<Order> getOrderWithID(@Query("courierUuid") String id);
}
