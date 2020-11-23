package ru.commandos.diner.delivery.controller;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.View;

import java.util.Optional;

import retrofit2.internal.EverythingIsNonNull;

@EverythingIsNonNull
public class ActivityGetter {

    public static Optional<Activity> getActivity(View view) {
        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return Optional.of((Activity) context);
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return Optional.empty();
    }
}
