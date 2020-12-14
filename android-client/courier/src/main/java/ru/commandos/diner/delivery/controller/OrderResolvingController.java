package ru.commandos.diner.delivery.controller;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import autodispose2.AutoDispose;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import retrofit2.internal.EverythingIsNonNull;
import ru.commandos.diner.delivery.model.Order;
import timber.log.Timber;

@EverythingIsNonNull
public class OrderResolvingController {

    public static final int FENCE_RADIUS = 500;
    private static final PublishSubject<Order> RX_GEO_FENCED_ORDERS = PublishSubject.create();
    private final Map<Order, Boolean> orders = new HashMap<>();
    private final GeofencingClient geoFencingClient;
    private final Activity activity;
    @Nullable
    private PendingIntent geoFencePendingIntent;

    public OrderResolvingController(AppCompatActivity activity) {
        geoFencingClient = LocationServices.getGeofencingClient(activity);
        this.activity = activity;
        GeoFencesBroadcastReceiver.getRxReceivedGeoFences()
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(activity)))
                .subscribe(geoFence -> {
                    Timber.d("GeoFence got: %s", geoFence.getRequestId());
                    orders.keySet().stream()
                            .filter(order -> order.getUuid().equals(geoFence.getRequestId()))
                            .forEach(order -> {
                                orders.put(order, true);
                                RX_GEO_FENCED_ORDERS.onNext(order);
                            });
                });
    }

    public static Observable<Order> getRxGeoFencedOrders() {
        return RX_GEO_FENCED_ORDERS;
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void start(Activity activity) {
        List<Geofence> geoFences = new ArrayList<>();
        orders.keySet().forEach(order -> geoFences.add(new Geofence.Builder()
                .setRequestId(order.getUuid())
                .setCircularRegion(order.getDestination().latitude,
                        order.getDestination().longitude, FENCE_RADIUS)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build()));
        if (geoFences.size() != 0) {
            Timber.d("Registered GeoFences: %s", geoFences.stream().map(Geofence::getRequestId).collect(Collectors.joining(", ")));
            geoFencingClient.addGeofences(getGeoFencingRequest(geoFences), getGeoFencePendingIntent())
                    .addOnSuccessListener(activity, aVoid -> Toast.makeText(activity,
                            "GeoFences успешно запущены", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(activity, Timber::e);
        } else {
            Timber.w("GeoFence пуст");
        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void stop(Activity activity) {
        geoFencingClient.removeGeofences(getGeoFencePendingIntent())
                .addOnSuccessListener(activity, aVoid -> Toast.makeText(activity,
                        "GeoFences успешно остановлены", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(activity, Timber::e);
    }

    public Map<Order, Boolean> getOrders() {
        return orders;
    }

    private GeofencingRequest getGeoFencingRequest(List<Geofence> geoFences) {
        return new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofences(geoFences)
                .build();
    }

    private PendingIntent getGeoFencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        return Optional.ofNullable(geoFencePendingIntent)
                .orElseGet(() -> {
                    Intent intent = new Intent(activity, GeoFencesBroadcastReceiver.class);
                    // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
                    // calling addGeoFences() and removeGeoFences().
                    geoFencePendingIntent = PendingIntent.getBroadcast(activity, 0, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    return geoFencePendingIntent;
                });
    }
}
