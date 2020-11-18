package ru.commandos.diner.delivery.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.internal.EverythingIsNonNull;
import ru.commandos.diner.delivery.model.Feature;
import ru.commandos.diner.delivery.model.Item;
import ru.commandos.diner.delivery.model.Order;
import ru.commandos.diner.delivery.model.OrderItems;

@EverythingIsNonNull
class ServerMock implements ServerApi {

    private Order order = null;

    public ServerMock() {
        randomOrder();
    }

    private void randomOrder() {
        Observable.interval(1, 15, TimeUnit.SECONDS).doOnNext(v -> {

            ArrayList<Item> items = new ArrayList<>();
            for (int i = 0; i < new Random().nextInt(4) + 1; i++) {
                String name = OrderItems.values()[new Random().nextInt(OrderItems.values().length)].toString();
                float mass = new Random().nextInt(4) + 0.99f;
                Feature[] features = new Feature[new Random().nextInt(3)];
                ArrayList<Feature> array = new ArrayList<>(Arrays.asList(Feature.values()));
                for (int j = 0; j < features.length; j++) {
                    features[j] = array.remove(new Random().nextInt(array.size()));
                }
                items.add(new Item(name, mass, features));
            }
            order = new Order(UUID.randomUUID(), items);
        }).subscribe();
    }

    @Override
    public Order getOrderWithID(String id) {
        return order;
    }
}
