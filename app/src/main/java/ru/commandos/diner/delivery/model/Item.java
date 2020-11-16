
package ru.commandos.diner.delivery.model;

public class Item {

    public String name;

    public float mass;

    public Feature[] features;

    public Item(String name, float mass, Feature[] features) {
        this.name = name;
        this.mass = mass;
        this.features = features;
    }
}