package ru.commandos.diner.delivery.controller;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import retrofit2.internal.EverythingIsNonNull;

@EverythingIsNonNull
public class SharedPreferencesHelper<M> {

    private static final String MY_SETTING_NAME = "shared_prefs_helper_data";
    private final SharedPreferences sharedPreferences;
    private final JsonAdapter<List<M>> listJsonAdapter;
    private final JsonAdapter<M> modelJsonAdapter;

    // Для работы хелпера необходим Контекст
    public SharedPreferencesHelper(@NonNull Context context, @NonNull Class<M> modelClass) {
        sharedPreferences = context.getSharedPreferences(MY_SETTING_NAME, Context.MODE_PRIVATE);
        Moshi moshi = new Moshi.Builder().build();
        listJsonAdapter = moshi.adapter(Types.newParameterizedType(List.class, modelClass));
        modelJsonAdapter = moshi.adapter(modelClass);
    }

    @Nullable
    public ArrayList<M> getModelsArrayList(@NonNull String listName) {
        return getListViaMoshi(listName);
    }

    public void saveModelsArrayList(@NonNull String listName, @NonNull ArrayList<M> models) {
        saveViaMoshi(listJsonAdapter, listName, models);
    }

    @Nullable
    public M getModel(@NonNull String modelName) {
        return getModelViaMoshi(modelName);
    }

    public void saveModel(@NonNull String modelName, @NonNull M model) {
        saveViaMoshi(modelJsonAdapter, modelName, model);
    }

    @Nullable
    private M getModelViaMoshi(String modelName) {
        try {
            String json = Optional.ofNullable(sharedPreferences.getString(modelName, null))
                    .orElseThrow(() -> new RuntimeException("Неправильное имя модели?"));
            return Optional.ofNullable(modelJsonAdapter.fromJson(json))
                    .orElseThrow(() -> new RuntimeException("Неправильный тип модели?"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private <T> void saveViaMoshi(JsonAdapter<T> jsonAdapter, String modelName, T model) {
        String json = jsonAdapter.toJson(model);
        sharedPreferences.edit()
                .putString(modelName, json)
                .apply();
    }

    @Nullable
    private ArrayList<M> getListViaMoshi(String listName) {
        try {
            String json = Optional.ofNullable(sharedPreferences.getString(listName, null))
                    .orElseThrow(() -> new RuntimeException("Неправильное имя модели?"));
            return Optional.ofNullable(listJsonAdapter.fromJson(json))
                    .map(ArrayList::new)
                    .orElseThrow(() -> new RuntimeException("Неправильный тип модели?"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}