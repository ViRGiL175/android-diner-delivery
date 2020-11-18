package ru.commandos.diner.delivery.model;

import java.util.List;
import java.util.UUID;

import retrofit2.internal.EverythingIsNonNull;

@EverythingIsNonNull
public class Order {

    public final String uuid;
    public final List<Item> items;

    public Order(UUID uuid, List<Item> items) {
        this.uuid = uuid.toString();
        this.items = items;
    }

    @Override
    public String toString() {
        return "Order{" +
                "uuid=" + uuid +
                ", items=" + items +
                '}';
    }
}
