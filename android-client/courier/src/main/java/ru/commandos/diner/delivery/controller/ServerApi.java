package ru.commandos.diner.delivery.controller;


import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.commandos.diner.delivery.model.Order;

public interface ServerApi {

    @GET("/delivery/check")
    Single<Order> getIncomingOrder(@Query("courierUuid") String courierUuid);

    @GET("/delivery/status")
    Single<List<Order>> getAllOrders(@Query("courierUuid") String courierUuid);

    @GET("/delivery/accept")
    Completable acceptOrder(@Query("courierUuid") String courierUuid,
                            @Query("orderUuid") String orderUuid);

    @GET("/delivery/deny")
    Completable denyOrder(@Query("courierUuid") String courierUuid,
                          @Query("orderUuid") String orderUuid);

    @GET("/delivery/handshake")
    void handshake(@Query("courierUuid") String courierUuid,
                          @Query("orderUuid") String orderUuid,
                          @Query("codeUuid") String codeUuid) throws Throwable;
}
