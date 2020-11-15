package ru.commandos.controller;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import ru.commandos.model.Item;
import ru.commandos.model.Order;
import ru.commandos.model.OrderDelivery;

@Service
public class DeliveryService implements DisposableBean {

    private final OrderNamesProvider orderNamesProvider;
    private final CouriersProvider couriersProvider;
    private final Map<UUID, Order> preparedOrders = new HashMap<>();
    private final List<Order> incomingOrders = new ArrayList<>();
    private final List<OrderDelivery> workingOrders = new ArrayList<>();

    public DeliveryService(@Autowired OrderNamesProvider orderNamesProvider,
                           @Autowired CouriersProvider couriersProvider) {
        this.orderNamesProvider = orderNamesProvider;
        this.couriersProvider = couriersProvider;
        setupOrdersGenerating();
        setupOrdersAssigning(couriersProvider);
    }

    private void setupOrdersAssigning(@Autowired CouriersProvider couriersProvider) {
        Observable.interval(10, 15, TimeUnit.SECONDS)
                .observeOn(RxJavaPlugins.createComputationScheduler(Thread::new))
                .filter(aLong -> !incomingOrders.isEmpty())
                .doOnNext(aLong -> preparedOrders.put(couriersProvider.courierUuids.get(0),
                        incomingOrders.remove(0)))
                .doOnNext(aLong -> Logger.trace("Assigned on " + aLong))
                .subscribe();
    }

    public Map<UUID, Order> getPreparedOrders() {
        return preparedOrders;
    }

    public List<OrderDelivery> getWorkingOrders() {
        return workingOrders;
    }

    public void setupOrdersGenerating() {
        Observable.interval(0, 10, TimeUnit.SECONDS)
                .observeOn(RxJavaPlugins.createComputationScheduler(Thread::new))
                .doOnNext(aLong -> {
                    Random random = new Random();
                    Order order = new Order(UUID.randomUUID());
                    int itemsCount = random.nextInt(5) + 1;
                    for (int i = 0; i < itemsCount; i++) {
                        Item item = new Item(orderNamesProvider.getRandom(random.nextBoolean()),
                                random.nextFloat() * 1000 + 200);
                        order.items.add(item);
                    }
                    incomingOrders.add(order);
                    Logger.trace("Generated with " + order.items.size() + " items");
                })
                .subscribe();
    }

    @Override
    public void destroy() throws Exception {

    }
}
