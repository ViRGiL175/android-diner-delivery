package ru.commandos.diner.delivery.controller;

import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ru.commandos.diner.delivery.model.Order;

public class OrderAcceptingController {

    private static final String TAG = "ORDERING";
    private final ArrayList<Order> acceptOrder = new ArrayList<>();
    private final CompositeDisposable compositeDisposable;
    private UUID courierUuid;
    private Order acceptableOrder = null;
    private ServerApi jsonApi = CourierService.getInstance().getJSONApi();

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
        Log.i(TAG, "Checking!");
//        jsonApi.yandex()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe((objectResponse, throwable) -> {
//                    Log.i(TAG, String.valueOf(objectResponse.code()));
//                    if (throwable != null) Log.e(TAG, throwable.getLocalizedMessage());
//                });
        compositeDisposable.add(Observable.interval(1, 5, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    Log.i("ORDERING", "On NEXT!");
                    jsonApi.getOrderWithID(courierUuid.toString())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe((orderResponse, throwable) -> {
                                Log.i(TAG, String.valueOf(orderResponse.code()));
                                if (throwable != null) Log.e(TAG, throwable.getLocalizedMessage());
                            });
                }, Throwable::printStackTrace));
    }
}
