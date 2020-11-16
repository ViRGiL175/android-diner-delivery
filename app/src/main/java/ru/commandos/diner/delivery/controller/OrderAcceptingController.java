package ru.commandos.diner.delivery.controller;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.commandos.diner.delivery.model.Order;

public class OrderAcceptingController {

    private UUID courierUuid;
    private final ArrayList<Order> acceptOrder = new ArrayList<>();
    private Order acceptableOrder = null;

    public OrderAcceptingController(String courierUuid) {
        this.courierUuid = UUID.fromString(courierUuid);
    }

    public ArrayList<Order> getAcceptOrderList() {
        return acceptOrder;
    }

    public Order getAcceptableOrder() {
        return acceptableOrder;
    }

    public void acceptAcceptableOrder() {
        acceptOrder.add(acceptableOrder);
        acceptableOrder = null;
    }

    public void denyAcceptableOrder() {
        acceptableOrder = null;
    }

    public void check() {
        Observable.interval(1, 5, TimeUnit.SECONDS)
                .observeOn(RxJavaPlugins.createComputationScheduler(Thread::new)).doOnNext(v -> {
            CourierServise.getInstance()
                    .getJSONApi()
                    .getOrderWithID(courierUuid.toString())
                    .enqueue(new Callback<Order>() {
                        @Override
                        public void onResponse(@NonNull Call<Order> call, @NonNull Response<Order> response) {
                            acceptableOrder = response.body();
                            Log.i("ORDERING", acceptableOrder.toString());
                        }

                        @Override
                        public void onFailure(@NonNull Call<Order> call, @NonNull Throwable t) {
                            t.printStackTrace();
                        }
                    });
        }).subscribe();
    }
}
