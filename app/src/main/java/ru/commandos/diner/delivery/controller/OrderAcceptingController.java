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
    private final UUID courierUuid;
    private final ServerApi jsonApi = CourierService.getInstance().getServerApi();
    private final MainActivity activity;
    private Order acceptableOrder = null;

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
