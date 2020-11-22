package ru.commandos.diner.delivery.controller;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;
import ru.commandos.diner.delivery.model.Order;
import ru.commandos.diner.delivery.view.MainActivity;

@EverythingIsNonNull
public class OrdersController {

    public static final String SHARED_PREFERENCES_ORDERS = "orders";
    public static final int REQUESTS_INTERVAL = 5;
    private final Observable<Response<Order>> incomingOrderObservable;
    private final ServerApi serverApi = HttpService.getInstance().getServerApi();
    private final SharedPreferencesHelper<Order> sharedPreferencesHelper;
    private final ArrayList<Order> acceptedOrders = new ArrayList<>();
    private final String courierUuid;
    @Nullable
    private Order incomingOrder;

    private void setIncomingOrder(Response<Order> response) {
        if (response.isSuccessful()) {
            incomingOrder = response.body();
        }
    }

    private void updateOrderList() {
        serverApi.getAllOrders(courierUuid).doOnSuccess(listResponse -> {
            if (listResponse.isSuccessful()) {
                acceptedOrders.clear();
                acceptedOrders.addAll(new ArrayList<>(listResponse.body()));
            }
        }).subscribe();
    }

    public OrdersController(String courierUuid, MainActivity mainActivity) {
        this.courierUuid = courierUuid;
        incomingOrderObservable = Observable.interval(1, REQUESTS_INTERVAL, TimeUnit.SECONDS)
                .flatMapSingle(aLong -> serverApi.getIncomingOrder(courierUuid))
                .doOnError(Throwable::printStackTrace)
                .doOnNext(this::setIncomingOrder);
        sharedPreferencesHelper = new SharedPreferencesHelper<>(mainActivity, Order.class);
        onResume();
    }

    public ArrayList<Order> getAcceptedOrders() {
        return acceptedOrders;
    }

    public void acceptOrder() {
        serverApi.acceptOrder(courierUuid, incomingOrder.getUuid()).subscribe(() -> {}).dispose();
        Optional.ofNullable(incomingOrder).ifPresent(acceptedOrders::add);
        updateOrderList();
        incomingOrder = null;
    }

    public void denyOrder() {
        serverApi.denyOrder(courierUuid, incomingOrder.getUuid()).subscribe(() -> {}).dispose();
        updateOrderList();
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

    public Observable<Response<Order>> getIncomingOrderObservable() {
        return incomingOrderObservable;
    }
}
