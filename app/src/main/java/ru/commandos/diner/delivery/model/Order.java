package ru.commandos.diner.delivery.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Order {

    //    @SerializedName("uuid")
//    @Expose
    public final UUID uuid;

    //    @SerializedName("items")
//    @Expose
    public List<Item> items = new ArrayList<>();

    public Order(UUID uuid, List<Item> items) {
        this.uuid = uuid;
        this.items = items;
    }

    public float getMass() {
        return 0;
    }

    @Override
    public String toString() {
        return "Order{" +
                "uuid=" + uuid +
                ", items=" + items +
                '}';
    }
}
