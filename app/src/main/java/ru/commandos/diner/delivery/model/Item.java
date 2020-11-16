package ru.commandos.diner.delivery.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Item {

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("mass")
    @Expose
    public float mass;

    @SerializedName("features")
    @Expose
    public Feature[] features;

    public Item(String name, float mass, Feature[] features) {
        this.name = name;
        this.mass = mass;
        this.features = features;
    }
}
