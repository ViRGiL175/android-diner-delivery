package ru.commandos.server.model;

import java.util.UUID;


public class OrderDelivery {

    public Order order;
    public UUID courierUuid;
    public DeliveryStatus status;

    public OrderDelivery(Order order, UUID courierUuid, DeliveryStatus status) {
        this.order = order;
        this.courierUuid = courierUuid;
        this.status = status;
    }
}

