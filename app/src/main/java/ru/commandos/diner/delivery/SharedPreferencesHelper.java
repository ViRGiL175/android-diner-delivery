package ru.commandos.diner.delivery;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SharedPreferencesHelper<M> {

    private static final String MY_SETTING_NAME = "shared_prefs_helper_data";
    private final Context context;
    private final SharedPreferences sharedPreferences;
    private final Class<M> modelClass;
    private final Moshi moshi = new Moshi.Builder().build();
    private final JsonAdapter<List<M>> listJsonAdapter;
    private final JsonAdapter<M> modelJsonAdapter;

    // Для работы хелпера необходим Контекст
    public SharedPreferencesHelper(@NonNull Context context, @NonNull Class<M> modelClass) {
        this.context = context;
        this.modelClass = modelClass;
        sharedPreferences = context.getSharedPreferences(MY_SETTING_NAME, Context.MODE_PRIVATE);
        listJsonAdapter = moshi.adapter(Types.newParameterizedType(List.class, modelClass));
        modelJsonAdapter = moshi.adapter(modelClass);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    public ArrayList<M> getModelsArrayList(@NonNull String listName) {
        return getListViaMoshi(listName);
    }

    public void saveModelsArrayList(@NonNull String listName, @NonNull ArrayList<M> models) {
        saveViaMoshi(listJsonAdapter, listName, models);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    public M getModel(@NonNull String modelName) {
        return getModelViaMoshi(modelName);
    }

    public void saveModel(@NonNull String modelName, @NonNull M model) {
        saveViaMoshi(modelJsonAdapter, modelName, model);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
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

    @RequiresApi(api = Build.VERSION_CODES.N)
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