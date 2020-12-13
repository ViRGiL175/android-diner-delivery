package ru.commandos.diner.delivery.controller;

import java.util.HashMap;
import java.util.Map;

import ru.commandos.diner.delivery.model.Order;

public class OrderResolvingController {

    private final Map<Order, Boolean> orders = new HashMap<>();

    public Map<Order, Boolean> getOrders() {
        return orders;
    }
}
