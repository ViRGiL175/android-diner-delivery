package ru.commandos.diner.server.controller;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.commandos.diner.server.model.DeliveryStatus;
import ru.commandos.diner.server.model.Item;
import ru.commandos.diner.server.model.Order;
import ru.commandos.diner.server.model.OrderDelivery;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class DeliveryService extends BaseComponent implements DisposableBean {

    private final OrderNamesProvider orderNamesProvider;
    private final CouriersProvider couriersProvider;
    private final LocationProvider locationProvider;
    private final HandshakeKeysProvider handshakeKeysProvider;
    /**
     * UUID - Courier UUID
     */
    // TODO: 007, 07.12.2020 Courier instead of UUID?
    private final Map<UUID, Order> preparedOrders = new HashMap<>();
    private final List<Order> incomingOrders = new ArrayList<>();
    /**
     * UUID - Order resolve key
     */
    private final Map<UUID, OrderDelivery> workingOrders = new HashMap<>();

    public DeliveryService(OrderNamesProvider orderNamesProvider, CouriersProvider couriersProvider,
                           LocationProvider locationProvider, HandshakeKeysProvider handshakeKeysProvider) {
        this.orderNamesProvider = orderNamesProvider;
        this.couriersProvider = couriersProvider;
        this.locationProvider = locationProvider;
        this.handshakeKeysProvider = handshakeKeysProvider;
        setupOrdersGenerating();
        setupOrdersAssigning(couriersProvider);
    }

    private void setupOrdersAssigning(@Autowired CouriersProvider couriersProvider) {
        Observable.interval(10, 15, TimeUnit.SECONDS)
                .observeOn(RxJavaPlugins.createComputationScheduler(Thread::new))
                .filter(aLong -> !incomingOrders.isEmpty())
                .doOnNext(aLong -> preparedOrders.put(couriersProvider.courierUuids.get(0),
                        incomingOrders.remove(0)))
                .doOnNext(aLong -> logger.info("Assigned on " + aLong))
                .subscribe();
    }

    public void setupOrdersGenerating() {
        Observable.interval(0, 10, TimeUnit.SECONDS).doOnNext(aLong -> {
            Random random = new Random();
            Order order = new Order(UUID.randomUUID(), locationProvider.getDinerLocation(),
                    locationProvider.getDestination());
            int itemsCount = random.nextInt(5) + 1;
            IntStream.range(0, itemsCount).boxed()
                    .map(integer -> orderNamesProvider.getRandom(random.nextBoolean()))
                    .map(rawItem -> new Item(rawItem.getKey(), random.nextFloat() * 1000 + 200, rawItem.getValue()))
                    .forEachOrdered(order.items::add);
            incomingOrders.add(order);
            logger.info("Generated with " + order.items.size() + " items");
        }).subscribe();
    }

    public void handshake(UUID courierUuid, UUID orderUuid, UUID codeUuid) {
        OrderDelivery orderDelivery = workingOrders.entrySet().stream()
                .filter(uuidOrderDeliveryEntry -> uuidOrderDeliveryEntry.getValue().courierUuid == courierUuid &&
                        uuidOrderDeliveryEntry.getValue().order.uuid == orderUuid)
                .filter(uuidOrderDeliveryEntry -> uuidOrderDeliveryEntry.getKey() == codeUuid)
                .findAny()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                .getValue();
        orderDelivery.status = DeliveryStatus.COMPLETED;
    }

    public void accept(UUID courierUuid, UUID orderUuid) {
        workingOrders.put(handshakeKeysProvider.get(), new OrderDelivery(preparedOrders.get(orderUuid),
                courierUuid, DeliveryStatus.PICKING_ORDER));
        preparedOrders.remove(orderUuid);
    }

    public List<OrderDelivery> status(UUID courierUuid) {
        return workingOrders.values().stream()
                .filter(orderDelivery -> orderDelivery.courierUuid.equals(courierUuid))
                .collect(Collectors.toList());
    }

    public Order check(UUID courierUuid) {
        return preparedOrders.entrySet().stream()
                .filter(uuidOrderEntry -> uuidOrderEntry.getKey().equals(courierUuid))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                .getValue();
    }

    @Override
    public void destroy() throws Exception {

    }
}
