package ru.commandos.diner.delivery.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import io.reactivex.rxjava3.core.Single;
import retrofit2.internal.EverythingIsNonNull;
import ru.commandos.diner.delivery.model.Feature;
import ru.commandos.diner.delivery.model.Item;
import ru.commandos.diner.delivery.model.Order;
import ru.commandos.diner.delivery.model.OrderItems;

@EverythingIsNonNull
class ServerMock implements ServerApi {

    private Order getRandomOrder() {
        ArrayList<Item> items = new ArrayList<>();
        for (int i = 0; i < new Random().nextInt(4) + 1; i++) {
            String name = OrderItems.values()[new Random().nextInt(OrderItems.values().length)]
                    .toString();
            float mass = new Random().nextInt(4) + 0.99f;
            Feature[] features = new Feature[new Random().nextInt(3)];
            ArrayList<Feature> array = new ArrayList<>(Arrays.asList(Feature.values()));
            for (int j = 0; j < features.length; j++) {
                features[j] = array.remove(new Random().nextInt(array.size()));
            }
            items.add(new Item(name, mass, features));
        }
        return new Order(UUID.randomUUID(), items);
    }

    @Override
    public Single<Map<String, Order>> getOrderWithID(String id) {
        return Single.just(Collections.singletonMap(id, getRandomOrder()));
    }
}
