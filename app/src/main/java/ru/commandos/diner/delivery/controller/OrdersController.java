package ru.commandos.diner.delivery.controller;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.internal.EverythingIsNonNull;
import ru.commandos.diner.delivery.model.Order;
import ru.commandos.diner.delivery.view.MainActivity;

@EverythingIsNonNull
public class OrdersController {

    public static final String SHARED_PREFERENCES_ORDERS = "orders";
    public static final int REQUESTS_INTERVAL = 5;
    private final Observable<Order> incomingOrderObservable;
    private final ServerApi serverApi = HttpService.getInstance().getServerApi();
    private final SharedPreferencesHelper<Order> sharedPreferencesHelper;
    private final ArrayList<Order> acceptedOrders = new ArrayList<>();
    @Nullable
    private Order incomingOrder;

    public OrdersController(String courierUuid, MainActivity mainActivity) {
        incomingOrderObservable = Observable.interval(1, REQUESTS_INTERVAL, TimeUnit.SECONDS)
                .flatMapSingle(aLong -> serverApi.getIncomingOrder(courierUuid))
                .doOnError(Throwable::printStackTrace)
                .doOnNext(order -> incomingOrder = order);
        sharedPreferencesHelper = new SharedPreferencesHelper<>(mainActivity, Order.class);
        onResume();
    }

    public ArrayList<Order> getAcceptedOrders() {
        return acceptedOrders;
    }

    public void acceptOrder() {
        Optional.ofNullable(incomingOrder).ifPresent(acceptedOrders::add);
        incomingOrder = null;
    }

    public void denyOrder() {
        incomingOrder = null;
    }

    public void onResume() {
        acceptedOrders.clear();
        acceptedOrders.addAll(Optional.ofNullable(sharedPreferencesHelper
                .getModelsArrayList(SHARED_PREFERENCES_ORDERS)).orElse(new ArrayList<>()));
    }

    public void onPause() {
        sharedPreferencesHelper.saveModelsArrayList(SHARED_PREFERENCES_ORDERS, acceptedOrders);
    }

    public Observable<Order> getIncomingOrderObservable() {
        return incomingOrderObservable;
    }
}
