package ru.commandos.diner.delivery.model;

import androidx.annotation.Nullable;

import java.util.Optional;

import retrofit2.internal.EverythingIsNonNull;

@EverythingIsNonNull
public class Item extends BaseApiModel {

    @Nullable
    protected final String name;
    protected final float mass;
    @Nullable
    protected final Feature[] features;

    public Item(@Nullable String name, float mass, @Nullable Feature[] features) {
        this.name = name;
        this.mass = mass;
        this.features = features;
    }

    public String getName() {
        return Optional.ofNullable(name).orElse(getNull());
    }

    public float getMass() {
        return mass;
    }

    public Feature[] getFeatures() {
        return Optional.ofNullable(features).orElse(new Feature[]{});
    }
}
