package ru.commandos.diner.delivery.model;

public class Item {

    public final String name;
    public final float mass;
    public final Feature[] features;

    public Item(String name, float mass, Feature[] features) {
        this.name = name;
        this.mass = mass;
        this.features = features;
    }
}
