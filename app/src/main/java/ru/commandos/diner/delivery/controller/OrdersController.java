package ru.commandos.diner.delivery.controller;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.internal.EverythingIsNonNull;
import ru.commandos.diner.delivery.MainActivity;
import ru.commandos.diner.delivery.model.Order;

import static autodispose2.AutoDispose.autoDisposable;

@EverythingIsNonNull
public class OrdersController {

    private final ArrayList<Order> acceptedOrders = new ArrayList<>();
    private final String courierUuid;
    private final ServerApi serverApi = HttpService.getInstance().getServerApi();
    private final MainActivity mainActivity;
    @Nullable
    private Order incomingOrder;

    public OrdersController(String courierUuid, MainActivity mainActivity) {
        this.courierUuid = courierUuid;
        this.mainActivity = mainActivity;
    }

    public ArrayList<Order> getAcceptOrderList() {
        return acceptedOrders;
    }

    @Nullable
    public Order getIncomingOrder() {
        return incomingOrder;
    }

    public void acceptOrder() {
        acceptedOrders.add(incomingOrder);
        incomingOrder = null;
        mainActivity.updateView();
    }

    public void denyOrder() {
        incomingOrder = null;
        mainActivity.updateView();
    }

    public void startChecking() {
        Observable.interval(1, 5, TimeUnit.SECONDS)
                .flatMapSingle(aLong -> serverApi.getOrderWithID(courierUuid))
                .to(autoDisposable(AndroidLifecycleScopeProvider.from(mainActivity)))
                .subscribe(stringOrderMap -> {
                    incomingOrder = stringOrderMap.get(courierUuid);
                    mainActivity.updateView();
                }, Throwable::printStackTrace);
    }
}
