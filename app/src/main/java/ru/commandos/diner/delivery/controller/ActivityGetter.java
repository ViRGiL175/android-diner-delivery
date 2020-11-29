package ru.commandos.diner.delivery.controller;

import android.content.Context;
import android.content.ContextWrapper;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Optional;

import retrofit2.internal.EverythingIsNonNull;

@EverythingIsNonNull
public class ActivityGetter {

    public static Optional<AppCompatActivity> getActivity(View view) {
        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof AppCompatActivity) {
                return Optional.of((AppCompatActivity) context);
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return Optional.empty();
    }
}
