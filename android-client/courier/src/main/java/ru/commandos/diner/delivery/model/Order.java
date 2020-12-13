package ru.commandos.diner.delivery.model;

import retrofit2.internal.EverythingIsNonNull;

@EverythingIsNonNull
public class Order extends ApiOrder {

    protected boolean resolvable = false;

    public Order(ApiOrder order) {
        super(order.uuid, order.items, order.dinerLocation, order.destination);
    }

    public boolean isResolvable() {
        return resolvable;
    }

    public void setResolvable(boolean resolvable) {
        this.resolvable = resolvable;
    }
}
