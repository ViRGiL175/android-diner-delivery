package ru.commandos.diner.delivery.controller;

import java.util.ArrayList;

import ru.commandos.diner.delivery.model.Order;

public class OrderAcceptingController {

    private final ArrayList<Order> acceptOrder = new ArrayList<>();
    private Order acceptableOrder = null;

    public ArrayList<Order> getAcceptOrderList() {
        return acceptOrder;
    }

    public Order getAcceptableOrder() {
        return acceptableOrder;
    }

    public void acceptAcceptableOrder() {
    }

    public void denyAcceptableOrder() {
    }
}
