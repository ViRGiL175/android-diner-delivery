package ru.commandos.diner.delivery.controller;

import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import ru.commandos.diner.delivery.MainActivity;
import ru.commandos.diner.delivery.model.Order;

public class OrderAcceptingController {

    private final ArrayList<Order> acceptOrder = new ArrayList<>();
    private final CompositeDisposable compositeDisposable;
    private UUID courierUuid;
    private Order acceptableOrder = null;
    private ServerApi jsonApi = CourierService.getInstance().getJSONApi();
    private MainActivity activity;

    public OrderAcceptingController(String courierUuid, CompositeDisposable compositeDisposable, MainActivity activity) {
        this.courierUuid = UUID.fromString(courierUuid);
        this.compositeDisposable = compositeDisposable;
        this.activity = activity;
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
        activity.updateView();
    }

    public void denyAcceptableOrder() {
        acceptableOrder = null;
        activity.updateView();
    }

    public void check() {
        Log.i("ORDERING", "Checking!");
        compositeDisposable.add(Observable.interval(1, 5, TimeUnit.SECONDS)
                .subscribe(aLong -> {
                    if (acceptableOrder == null) {
                        acceptableOrder = jsonApi.getOrderWithID(courierUuid.toString());
                        Log.i("ORDERING", "On NEXT!: " + acceptableOrder);
                        activity.updateView();
                    }
                }, Throwable::printStackTrace));
    }
}
