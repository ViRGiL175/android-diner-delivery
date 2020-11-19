package ru.commandos.diner.delivery.controller;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.internal.EverythingIsNonNull;
import ru.commandos.diner.delivery.MainActivity;
import ru.commandos.diner.delivery.SharedPreferencesHelper;
import ru.commandos.diner.delivery.model.Order;
import timber.log.Timber;

@EverythingIsNonNull
public class OrdersController {

    public static final String SHARED_PREFERENCES_ORDERS = "orders";
    public static final int REQUESTS_INTERVAL = 5;
    private final ArrayList<Order> acceptedOrders = new ArrayList<>();
    private final ServerApi serverApi = HttpService.getInstance().getServerApi();
    private final Observable<Order> orderObservable;
    private final SharedPreferencesHelper<Order> sharedPreferencesHelper;
    @Nullable
    private Order incomingOrder;

    public OrdersController(String courierUuid, MainActivity mainActivity) {
        orderObservable = Observable.interval(1, REQUESTS_INTERVAL, TimeUnit.SECONDS)
                .doOnNext(aLong -> Timber.i("Timer works!"))
                .flatMapSingle(aLong -> serverApi.getOrderWithID(courierUuid))
                .doOnError(Throwable::printStackTrace)
                .map(stringOrderMap -> stringOrderMap.get(courierUuid))
                .doOnNext(order -> incomingOrder = order);
        sharedPreferencesHelper = new SharedPreferencesHelper<>(mainActivity, Order.class);
    }

    public ArrayList<Order> getAcceptedOrders() {
        return acceptedOrders;
    }

    public void acceptOrder() {
        if (incomingOrder != null) {
            acceptedOrders.add(incomingOrder);
        }
        incomingOrder = null;
    }

    public void denyOrder() {
        incomingOrder = null;
    }

    public void onDestroy() {
        sharedPreferencesHelper.saveModelsArrayList(SHARED_PREFERENCES_ORDERS, acceptedOrders);
    }

    public Observable<Order> getOrderObservable() {
        return orderObservable;
    }
}
