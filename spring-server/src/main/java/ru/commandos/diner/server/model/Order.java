package ru.commandos.diner.server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Order {

    public final UUID uuid;
    public final List<Item> items = new ArrayList<>();
    public final Location dinerLocation;
    public final Location destination;

    public Order(UUID uuid, Location dinerLocation, Location destination) {
        this.uuid = uuid;
        this.dinerLocation = dinerLocation;
        this.destination = destination;
    }
}
