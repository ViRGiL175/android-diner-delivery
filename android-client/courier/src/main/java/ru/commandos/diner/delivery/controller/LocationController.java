package ru.commandos.diner.delivery.controller;

import android.app.Activity;
import android.location.Location;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import retrofit2.internal.EverythingIsNonNull;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

@EverythingIsNonNull
public class LocationController extends LocationCallback implements OnMapReadyCallback {

    private final FusedLocationProviderClient fusedLocationClient;
    @Nullable
    private GoogleMap googleMap;
    @Nullable
    private Location courierLocation;

    public LocationController(Activity activity) {
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
        if (location != null) {
            showCourier(location, 5);
            courierLocation = location;
        }
    }

    public void showCourier(Location location, float zoom) {
        LatLng courier = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions().position(courier).title("Вы"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(courier));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(zoom));
    }

    @Override
    public void onLocationResult(LocationResult locationResult) {
        Location location = locationResult.getLastLocation();
        showCourier(location, 13);
        courierLocation = location;
    }

    private LocationRequest createLocationRequest() {
        return LocationRequest.create()
                .setInterval(10000)
                .setFastestInterval(5000)
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }
}
