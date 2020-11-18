package ru.commandos.diner.delivery.controller;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import retrofit2.internal.EverythingIsNonNull;
import ru.commandos.diner.delivery.MainActivity;
import ru.commandos.diner.delivery.model.Order;

@EverythingIsNonNull
public class OrderAcceptingController {

    private final ArrayList<Order> acceptOrder = new ArrayList<>();
    private final CompositeDisposable compositeDisposable;
    private final UUID courierUuid;
    private final ServerApi serverApi = CourierService.getInstance().getServerApi();
    private final MainActivity activity;
    @Nullable
    private Order acceptableOrder;

    public OrderAcceptingController(String courierUuid, CompositeDisposable compositeDisposable, MainActivity activity) {
        this.courierUuid = UUID.fromString(courierUuid);
        this.compositeDisposable = compositeDisposable;
        this.activity = activity;
    }

    public ArrayList<Order> getAcceptOrderList() {
        return acceptOrder;
    }

    @Nullable
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
        Observable.interval(1, 5, TimeUnit.SECONDS)
                .doOnNext(o -> serverApi.getOrderWithID(courierUuid.toString())
                        .doOnEvent((order, throwable) -> {
                            if (throwable == null) {
                                acceptableOrder = order;
                                activity.updateView();
                                Log.i("ORDERING", "On NEXT!: " + acceptableOrder);
                            } else {
                                Toast.makeText(activity, throwable.getLocalizedMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        })
                        .subscribe())
                .subscribe();
    }
}
