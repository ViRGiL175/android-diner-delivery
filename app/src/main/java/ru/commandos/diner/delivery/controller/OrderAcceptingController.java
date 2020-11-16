package ru.commandos.diner.delivery.controller;

import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import ru.commandos.diner.delivery.model.Order;

public class OrderAcceptingController {

    private final ArrayList<Order> acceptOrder = new ArrayList<>();
    private final CompositeDisposable compositeDisposable;
    private UUID courierUuid;
    private Order acceptableOrder = null;
    private JSONPlaceHolderApi jsonApi = CourierService.getInstance()
            .getJSONApi();

    public OrderAcceptingController(String courierUuid, CompositeDisposable compositeDisposable) {
        this.courierUuid = UUID.fromString(courierUuid);
        this.compositeDisposable = compositeDisposable;
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
        Log.i("ORDERING", "Checking!");
        compositeDisposable.add(Observable.interval(1, 5, TimeUnit.SECONDS)
                .subscribe(aLong -> {
                    Log.i("ORDERING", "On NEXT!");
                    jsonApi.getOrderWithID(courierUuid.toString())
                            .subscribe(order -> Log.i("ORDERING", order.uuid.toString()),
                                    Throwable::printStackTrace);
                }, Throwable::printStackTrace));
    }
}
