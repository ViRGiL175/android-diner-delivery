package ru.commandos.diner.delivery.model;

import retrofit2.internal.EverythingIsNonNull;

@EverythingIsNonNull
abstract class BaseApiModel {

    protected String getNull() {
        return "NUll PAI Response";
    }
}
