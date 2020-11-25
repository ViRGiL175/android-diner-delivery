package ru.commandos.diner.server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Order {

    public final UUID uuid;
    public final List<Item> items = new ArrayList<>();

    public Order(UUID uuid) {
        this.uuid = uuid;
    }
}
