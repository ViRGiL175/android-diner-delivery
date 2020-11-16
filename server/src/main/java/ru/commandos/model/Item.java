package ru.commandos.model;

import java.util.Arrays;

public class Item {

    public final String name;
    public final float mass;
    public final Feature[] features;

    public Item(String name, float mass, Feature[] features) {
        this.name = name;
        this.mass = mass;
        this.features = features;
    }

    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", mass=" + mass +
                ", features=" + Arrays.toString(features) +
                '}';
    }
}
