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
import ru.commandos.diner.delivery.model.ApiOrder;
import ru.commandos.diner.delivery.model.Feature;
import ru.commandos.diner.delivery.model.Item;
import ru.commandos.diner.delivery.model.Location;
import ru.commandos.diner.delivery.model.OrderItems;

@EverythingIsNonNull
class ServerMock implements ServerApi {

    public static final int SERVER_DELAY = 3;
    public static final Location mockLocation = new Location(56.85306, 53.21222);

    private final List<ApiOrder> apiOrders = new ArrayList<>();

    {
        apiOrders.add(getRandomOrder());
    }

    private ApiOrder getRandomOrder() {
        return getRandomOrder(UUID.randomUUID());
    }

    private ApiOrder getRandomOrder(UUID uuid) {
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
        return new ApiOrder(uuid.toString(), items, mockLocation, new Location(
                mockLocation.latitude + (Math.random() - 1) / 5,
                mockLocation.longitude + (Math.random() - 1) / 5));
    }

    @Override
    public Single<ApiOrder> getIncomingOrder(String courierUuid) {
        return Single.just(getRandomOrder()).delay(SERVER_DELAY, TimeUnit.SECONDS);
    }

    @Override
    public Single<List<ApiOrder>> getAllOrders(String courierUuid) {
        return Single.just(apiOrders).delay(SERVER_DELAY, TimeUnit.SECONDS);
    }

    @Override
    public Completable acceptOrder(String courierUuid, String orderUuid) {
        return Completable.fromAction(() -> apiOrders.add(getRandomOrder(UUID.fromString(orderUuid))))
                .delay(SERVER_DELAY, TimeUnit.SECONDS);
    }

    @Override
    public Completable denyOrder(String courierUuid, String orderUuid) {
        return Completable.fromAction(() -> apiOrders
                .removeIf(order -> order.getUuid().equals(orderUuid)))
                .delay(SERVER_DELAY, TimeUnit.SECONDS);
    }
}
