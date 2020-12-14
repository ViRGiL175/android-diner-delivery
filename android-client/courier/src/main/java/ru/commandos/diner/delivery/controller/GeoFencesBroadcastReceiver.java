package ru.commandos.diner.delivery.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import retrofit2.internal.EverythingIsNonNull;
import timber.log.Timber;

@EverythingIsNonNull
public class GeoFencesBroadcastReceiver extends BroadcastReceiver {

    private static final PublishSubject<Geofence> RX_RECEIVED_GEO_FENCES = PublishSubject.create();

    public static Observable<Geofence> getRxReceivedGeoFences() {
        return RX_RECEIVED_GEO_FENCES;
    }

    public void onReceive(Context context, Intent intent) {
        Timber.d("Some Intent received: %s", intent);
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode());
            Timber.e(errorMessage);
            return;
        }

        // Get the transition type.
        int geoFenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geoFences that were triggered. A single event can trigger
            // multiple geoFences.
            List<Geofence> triggeringGeoFences = geofencingEvent.getTriggeringGeofences();

            triggeringGeoFences.forEach((RX_RECEIVED_GEO_FENCES)::onNext);

//            // Get the transition details as a String.
//            String geoFenceTransitionDetails = getGeoFenceTransitionDetails(
//                    this,
//                    geoFenceTransition,
//                    triggeringGeoFences
//            );
//
//            // Send notification and log the transition details.
//            sendNotification(geoFenceTransitionDetails);
//            Timber.i(geoFenceTransitionDetails);
        } else {
            // Log the error.
            Timber.e("GeoFence Error Code: %s", geofencingEvent.getErrorCode());
        }
    }
}
