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
import ru.commandos.diner.delivery.model.OfflineState;
import ru.commandos.diner.delivery.model.Order;

import static autodispose2.AutoDispose.autoDisposable;

@EverythingIsNonNull
public class OrdersController {

    public static final String SHARED_PREFERENCES_ORDERS = "orders";
    public static final String SHARED_PREFERENCES_OFFLINE = "offline";
    public static final int REQUESTS_INCOMING_ORDER_INTERVAL = 5;
    public static final int REQUEST_UPDATE_LIST_INTERVAL = 15;
    private final PublishSubject<Order> incomingOrderPublishSubject = PublishSubject.create();
    private final Observable<Order> incomingOrderObservable;
    private final PublishSubject<List<Order>> acceptedOrdersPublishSubject = PublishSubject.create();
    private final Observable<List<Order>> acceptedOrdersObservable;
    private final ServerApi serverApi = HttpService.getInstance().getServerApi();
    private final SharedPreferencesHelper<Order> sharedPreferencesHelper;
    private final SharedPreferencesHelper<OfflineState> sharedPreferencesHelperOffline;
    private final AppCompatActivity activity;
    private final ArrayList<Order> acceptedOrders = new ArrayList<>();
    private final String courierUuid;
    private boolean offlineMode = true;
    private OfflineState offlineState = new OfflineState();
    @Nullable
    private Order incomingOrder;

    public OrdersController(String courierUuid, AppCompatActivity activity) {
        this.courierUuid = courierUuid;
        this.activity = activity;
        incomingOrderObservable = Observable.merge(incomingOrderPublishSubject,
                Observable.interval(1, REQUESTS_INCOMING_ORDER_INTERVAL, TimeUnit.SECONDS)
                        .filter(aLong -> !offlineMode)
                        .flatMapSingle(aLong -> serverApi.getIncomingOrder(courierUuid))
                        .doOnError(Throwable::printStackTrace)
                        .doOnNext(this::assignIncomingOrder));
        acceptedOrdersObservable = Observable.merge(acceptedOrdersPublishSubject,
                Observable.interval(1, REQUEST_UPDATE_LIST_INTERVAL, TimeUnit.SECONDS)
                        .filter(aLong -> !offlineMode)
                        .flatMapSingle(aLong -> serverApi.getAllOrders(courierUuid))
                        .doOnError(Throwable::printStackTrace)
                        .doOnNext(this::assignAcceptedOrders));
        sharedPreferencesHelper = new SharedPreferencesHelper<>(activity, Order.class);
        sharedPreferencesHelperOffline = new SharedPreferencesHelper<OfflineState>(activity, OfflineState.class);
        onResume();
    }

    private void assignIncomingOrder(Order order) {
        incomingOrder = order;
    }

    private void assignAcceptedOrders(List<Order> orders) {
        acceptedOrders.clear();
        acceptedOrders.addAll(orders);
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

    public void enterToOfflineMode() {
        offlineMode = true;
    }

    public void exitFromOfflineMode() {
        offlineMode = false;
    }

    public void onResume() {
        acceptedOrders.clear();
        acceptedOrders.addAll(Optional.ofNullable(sharedPreferencesHelper
                .getModelsArrayList(SHARED_PREFERENCES_ORDERS)).orElse(new ArrayList<>()));

        offlineState = (Optional.ofNullable(sharedPreferencesHelperOffline
                .getModel(SHARED_PREFERENCES_OFFLINE)).orElse(new OfflineState()));
        offlineMode = offlineState.offlineMode;
    }

    public void onPause() {
        sharedPreferencesHelper.saveModelsArrayList(SHARED_PREFERENCES_ORDERS, acceptedOrders);

        offlineState.offlineMode = offlineMode;
        sharedPreferencesHelperOffline.saveModel(SHARED_PREFERENCES_OFFLINE, offlineState);
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