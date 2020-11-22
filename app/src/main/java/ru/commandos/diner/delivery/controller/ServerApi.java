package ru.commandos.diner.delivery.controller;


import java.util.Map;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.commandos.diner.delivery.model.Order;

public interface ServerApi {

    @GET("/delivery/check")
    Single<Map<String, Order>> getOrderWithID(@Query("courierUuid") String id);
}
