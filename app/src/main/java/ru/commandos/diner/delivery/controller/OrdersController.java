package ru.commandos.diner.delivery.controller;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import autodispose2.AutoDispose;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;
import ru.commandos.diner.delivery.model.Order;
import ru.commandos.diner.delivery.view.MainActivity;

@EverythingIsNonNull
public class OrdersController {

    public static final String SHARED_PREFERENCES_ORDERS = "orders";
    public static final int REQUESTS_INCOMING_ORDER_INTERVAL = 5;
    public static final int REQUEST_UPDATE_LIST_INTERVAL = 15;
    private final Observable<Response<Order>> incomingOrderObservable;
    private final ServerApi serverApi = HttpService.getInstance().getServerApi();
    private final SharedPreferencesHelper<Order> sharedPreferencesHelper;
    private final ArrayList<Order> acceptedOrders = new ArrayList<>();
    private final String courierUuid;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    @Nullable
    private Order incomingOrder;

    private void setIncomingOrder(Response<Order> response) {
        if (response.isSuccessful()) {
            incomingOrder = response.body();
        }
    }

    private void updateOrderList() {
        serverApi.getAllOrders(courierUuid).doOnSuccess(listResponse -> {
            if (listResponse.isSuccessful() && listResponse.body() != null) {
                acceptedOrders.clear();
                acceptedOrders.addAll(new ArrayList<>(listResponse.body()));
            }
        }).subscribe();
    }

    public OrdersController(String courierUuid, MainActivity mainActivity) {
        this.courierUuid = courierUuid;
        incomingOrderObservable = Observable.interval(1, REQUESTS_INCOMING_ORDER_INTERVAL, TimeUnit.SECONDS)
                .flatMapSingle(aLong -> serverApi.getIncomingOrder(courierUuid))
                .doOnError(Throwable::printStackTrace)
                .doOnNext(this::setIncomingOrder);
        Observable.interval(1, REQUEST_UPDATE_LIST_INTERVAL, TimeUnit.SECONDS)
                .doOnNext(aLong -> updateOrderList()).subscribe();
        sharedPreferencesHelper = new SharedPreferencesHelper<>(mainActivity, Order.class);
        onResume();
    }

    public ArrayList<Order> getAcceptedOrders() {
        return acceptedOrders;
    }

    public void acceptOrder() {
        Optional.ofNullable(incomingOrder).ifPresent(order -> {
                    serverApi.acceptOrder(courierUuid, order.getUuid())
                            .to(AutoDispose.autoDisposable(CompletableObserver::onComplete))
                            .subscribe(this::updateOrderList);
                    acceptedOrders.add(order);
                }
        );
        incomingOrder = null;
    }

    public void denyOrder() {
        Optional.ofNullable(incomingOrder).ifPresent(order -> {
                    serverApi.denyOrder(courierUuid, order.getUuid())
                            .to(AutoDispose.autoDisposable(CompletableObserver::onComplete))
                            .subscribe(this::updateOrderList);
                    acceptedOrders.add(order);
                }
        );
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
