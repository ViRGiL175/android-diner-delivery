package ru.commandos.diner.delivery.controller;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import retrofit2.internal.EverythingIsNonNull;
import ru.commandos.diner.delivery.model.Order;
import ru.commandos.diner.delivery.view.MainActivity;

import static autodispose2.AutoDispose.autoDisposable;

@EverythingIsNonNull
public class OrdersController {

    public static final String SHARED_PREFERENCES_ORDERS = "orders";
    public static final int REQUESTS_INCOMING_ORDER_INTERVAL = 5;
    public static final int REQUEST_UPDATE_LIST_INTERVAL = 15;
    private final PublishSubject<Order> incomingOrderPublishSubject = PublishSubject.create();
    private final PublishSubject<List<Order>> acceptedOrdersPublishSubject = PublishSubject.create();
    private final ServerApi serverApi = HttpService.getInstance().getServerApi();
    private final SharedPreferencesHelper<Order> sharedPreferencesHelper;
    private final AppCompatActivity activity;
    private final ArrayList<Order> acceptedOrders = new ArrayList<>();
    private final String courierUuid;
    private boolean offlineMode = false;
    //TODO: 28.11.2020 00:58 Разобраться с полями Observable, которые null
    private Observable<Order> incomingOrderObservable;
    private Observable<List<Order>> acceptedOrdersObservable;
    @Nullable
    private Order incomingOrder;

    private void assignIncomingOrder(Order order) {
        incomingOrder = order;
    }

    private void assignAcceptedOrders(List<Order> orders) {
        acceptedOrders.clear();
        acceptedOrders.addAll(orders);
    }

    private void startObserveIncomingOrders() {
        incomingOrderObservable = Observable.merge(incomingOrderPublishSubject,
                Observable.interval(1, REQUESTS_INCOMING_ORDER_INTERVAL, TimeUnit.SECONDS)
                        .takeUntil(aLong -> offlineMode)
                        .flatMapSingle(aLong -> serverApi.getIncomingOrder(courierUuid))
                        .doOnError(Throwable::printStackTrace)
                        .doOnNext(this::assignIncomingOrder));
    }

    private void startObserveAcceptedOrders() {
        acceptedOrdersObservable = Observable.merge(acceptedOrdersPublishSubject,
                Observable.interval(1, REQUEST_UPDATE_LIST_INTERVAL, TimeUnit.SECONDS)
                        .takeUntil(aLong -> offlineMode)
                        .flatMapSingle(aLong -> serverApi.getAllOrders(courierUuid))
                        .doOnError(Throwable::printStackTrace)
                        .doOnNext(this::assignAcceptedOrders));
    }

    public OrdersController(String courierUuid, AppCompatActivity activity) {
        this.courierUuid = courierUuid;
        this.activity = activity;
        startObserveIncomingOrders();
        startObserveAcceptedOrders();
        sharedPreferencesHelper = new SharedPreferencesHelper<>(activity, Order.class);
        onResume();
    }

    public void acceptOrder() {
        Optional.ofNullable(incomingOrder)
                .ifPresent(order -> serverApi.acceptOrder(courierUuid, order.getUuid())
                        .andThen(Completable.fromAction(() -> incomingOrder = null))
                        .andThen(serverApi.getAllOrders(courierUuid))
                        .doOnError(Throwable::printStackTrace)
                        .to(autoDisposable(AndroidLifecycleScopeProvider.from(activity)))
                        .subscribe(orders -> {
                            assignAcceptedOrders(orders);
                            acceptedOrdersPublishSubject.onNext(orders);
                        }));
    }

    public void denyOrder() {
        Optional.ofNullable(incomingOrder)
                .ifPresent(order -> serverApi.denyOrder(courierUuid, order.getUuid())
                        .andThen(Completable.fromAction(() -> incomingOrder = null))
                        .andThen(serverApi.getAllOrders(courierUuid))
                        .doOnError(Throwable::printStackTrace)
                        .to(autoDisposable(AndroidLifecycleScopeProvider.from(activity)))
                        .subscribe(orders -> {
                            assignAcceptedOrders(orders);
                            acceptedOrdersPublishSubject.onNext(orders);
                        }));
    }

    public void entryOnOfflineMode() {
        offlineMode = true;
    }

    public void exitFromOfflineMode() {
        offlineMode = false;
        startObserveIncomingOrders();
        startObserveAcceptedOrders();
        ((MainActivity) activity).updateOrderControllerProperties();
    }

    public void onResume() {
        acceptedOrders.clear();
        acceptedOrders.addAll(Optional.ofNullable(sharedPreferencesHelper
                .getModelsArrayList(SHARED_PREFERENCES_ORDERS)).orElse(new ArrayList<>()));
    }

    public void onPause() {
        sharedPreferencesHelper.saveModelsArrayList(SHARED_PREFERENCES_ORDERS, acceptedOrders);
    }

    public Observable<List<Order>> getAcceptedOrdersObservable() {
        return acceptedOrdersObservable;
    }

    public ArrayList<Order> getAcceptedOrders() {
        return acceptedOrders;
    }

    public Observable<Order> getIncomingOrderObservable() {
        return incomingOrderObservable;
    }
}
