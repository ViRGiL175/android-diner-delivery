package ru.commandos.diner.delivery.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Order {

    public final UUID uuid;
    public final List<Item> items = new ArrayList<>();

    public Order(UUID uuid) {
        this.uuid = uuid;
    }

    public float getMass() {
        return 0;
    }
}
