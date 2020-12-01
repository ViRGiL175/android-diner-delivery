package ru.commandos.diner.delivery.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.internal.EverythingIsNonNull;
import ru.commandos.diner.delivery.model.Feature;
import ru.commandos.diner.delivery.model.Item;
import ru.commandos.diner.delivery.model.Order;
import ru.commandos.diner.delivery.model.OrderItems;

@EverythingIsNonNull
class ServerMock implements ServerApi {

    public static final int SERVER_DELAY = 3;

    private final List<Order> orders = new ArrayList<>();

    {
        orders.add(getRandomOrder());
    }

    private Order getRandomOrder() {
        return getRandomOrder(UUID.randomUUID());
    }

    private Order getRandomOrder(UUID uuid) {
        Random random = new Random();
        ArrayList<Item> items = new ArrayList<>();
        for (int i = 0; i < random.nextInt(4) + 1; i++) {
            String name = OrderItems.values()[random.nextInt(OrderItems.values().length)]
                    .toString();
            float mass = random.nextInt(4) + 0.99f;
            Feature[] features = new Feature[random.nextInt(3)];
            ArrayList<Feature> array = new ArrayList<>(Arrays.asList(Feature.values()));
            for (int j = 0; j < features.length; j++) {
                features[j] = array.remove(random.nextInt(array.size()));
            }
            items.add(new Item(name, mass, features));
        }
        return new Order(uuid.toString(), items);
    }

    @Override
    public Single<Order> getIncomingOrder(String courierUuid) {
        return Single.just(getRandomOrder()).delay(SERVER_DELAY, TimeUnit.SECONDS);
    }

    @Override
    public Single<List<Order>> getAllOrders(String courierUuid) {
        return Single.just(orders).delay(SERVER_DELAY, TimeUnit.SECONDS);
    }

    @Override
    public Completable acceptOrder(String courierUuid, String orderUuid) {
        return Completable.fromAction(() -> orders.add(getRandomOrder(UUID.fromString(orderUuid))))
                .delay(SERVER_DELAY, TimeUnit.SECONDS);
    }

    @Override
    public Completable denyOrder(String courierUuid, String orderUuid) {
        return Completable.fromAction(() -> orders
                .removeIf(order -> order.getUuid().equals(orderUuid)))
                .delay(SERVER_DELAY, TimeUnit.SECONDS);
    }
}
