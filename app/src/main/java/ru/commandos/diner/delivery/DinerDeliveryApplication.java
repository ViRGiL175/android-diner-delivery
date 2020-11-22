package ru.commandos.diner.delivery;

import android.app.Application;

import androidx.annotation.NonNull;

import timber.log.Timber;

public class DinerDeliveryApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }
    }

    private static class CrashReportingTree extends Timber.Tree {

        @Override
        protected void log(int priority, String tag, @NonNull String message, Throwable t) {
            // Логика логгирования в релизной версии
        }
    }
}
