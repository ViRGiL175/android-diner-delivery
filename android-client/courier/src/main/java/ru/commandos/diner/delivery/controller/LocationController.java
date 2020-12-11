package ru.commandos.diner.delivery.controller;

import android.location.Location;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import retrofit2.internal.EverythingIsNonNull;
import ru.commandos.diner.delivery.model.Order;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

@EverythingIsNonNull
public class LocationController extends LocationCallback implements OnMapReadyCallback {

    private final FusedLocationProviderClient fusedLocationClient;
    private final List<Marker> acceptedOrdersMarkers = new ArrayList<>();
    @Nullable
    private GoogleMap googleMap;
    @Nullable
    private List<Order> acceptedOrders;
    @Nullable
    private Marker courier;
    @Nullable
    private Marker incomingDiner;
    @Nullable
    private Marker incomingDestination;

    public LocationController(AppCompatActivity activity) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
    }

    @Override
    @RequiresPermission(allOf = {ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION})
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        fusedLocationClient.getLastLocation().addOnSuccessListener(this::onLastLocationGot);
        fusedLocationClient.requestLocationUpdates(createLocationRequest(), this, Looper.getMainLooper());
    }

    private void onLastLocationGot(Location location) {
        Optional.ofNullable(location).ifPresent(l -> showCourier(new LatLng(location.getLatitude(),
                location.getLongitude())));
    }

    public void showCourier(LatLng courier) {
        Optional.ofNullable(this.courier).orElseGet(() -> {
            this.courier = googleMap.addMarker(new MarkerOptions().position(courier).title("Вы"));
            return this.courier;
        }).setPosition(courier);
    }

    public void showIncomingOrder(Order order) {
        if (order != null) {
            Optional.ofNullable(incomingDiner).orElseGet(() -> {
                incomingDiner = googleMap.addMarker(new MarkerOptions().position(order
                        .getDinerLocation()).title("Потенциальный Дайнер"));
                return incomingDiner;
            });
            incomingDiner.setVisible(true);
            incomingDiner.setPosition(order.getDinerLocation());
            Optional.ofNullable(incomingDestination).orElseGet(() -> {
                incomingDestination = googleMap.addMarker(new MarkerOptions().position(order
                        .getDestination()).title("Потенциальный Клиент"));
                return incomingDestination;
            });
            incomingDestination.setVisible(true);
            incomingDestination.setPosition(order.getDestination());
        } else {
            Optional.ofNullable(incomingDiner).ifPresent(marker -> marker.setVisible(false));
            Optional.ofNullable(incomingDestination).ifPresent(marker -> marker.setVisible(false));
        }
    }

    public void updateAcceptedOrders() {
        acceptedOrdersMarkers.forEach(Marker::remove);
        acceptedOrders.forEach(order -> {
            acceptedOrdersMarkers.add(googleMap.addMarker(new MarkerOptions().position(order
                    .getDinerLocation()).title("Дайнер")));
            acceptedOrdersMarkers.add(googleMap.addMarker(new MarkerOptions().position(order
                    .getDestination()).title("Клиент")));
        });
    }

    public void showAllOnMap() {
        List<Marker> markers = new ArrayList<>();
        Optional.ofNullable(courier).ifPresent(markers::add);
        Optional.ofNullable(incomingDestination).ifPresent(markers::add);
        Optional.ofNullable(incomingDiner).ifPresent(markers::add);
        markers.addAll(acceptedOrdersMarkers);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        markers.forEach(marker -> builder.include(marker.getPosition()));
        if (markers.size() != 0) {
            LatLngBounds bounds = builder.build();
            int cameraPadding = (int) DpConverter.convertDpToPixel(72);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, cameraPadding);
            googleMap.animateCamera(cameraUpdate);
        }
    }

    @Override
    public void onLocationResult(LocationResult locationResult) {
        Location location = locationResult.getLastLocation();
        showCourier(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    private LocationRequest createLocationRequest() {
        return LocationRequest.create()
                .setInterval(10000)
                .setFastestInterval(5000)
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    public void setAcceptedOrders(ArrayList<Order> acceptedOrders) {
        this.acceptedOrders = acceptedOrders;
    }
}
