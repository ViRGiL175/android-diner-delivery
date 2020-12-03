package ru.commandos.diner.delivery.model;

import retrofit2.internal.EverythingIsNonNull;

@EverythingIsNonNull
public class Location extends BaseApiModel {

    public final double latitude;
    public final double longitude;

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
