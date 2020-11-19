package ru.commandos.diner.delivery.model;

import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import retrofit2.internal.EverythingIsNonNull;

@EverythingIsNonNull
public class Order extends BaseApiModel {

    @Nullable
    protected final String uuid;
    @Nullable
    protected final List<Item> items;

    public Order(@Nullable String uuid, @Nullable List<Item> items) {
        this.uuid = uuid;
        this.items = items;
    }

    public String getUuid() {
        return Optional.ofNullable(uuid).orElse("00000000-0000-0000-0000-000000000000");
    }

    public List<Item> getItems() {
        return Optional.ofNullable(items).orElse(Collections.singletonList(
                new Item(null, 0, null)));
    }

    @Override
    public String toString() {
        return "Order{" +
                "uuid=" + uuid +
                ", items=" + items +
                '}';
    }
}
