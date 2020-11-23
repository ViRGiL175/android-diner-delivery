package ru.commandos.diner.delivery.controller;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import retrofit2.internal.EverythingIsNonNull;
import ru.commandos.diner.delivery.R;
import ru.commandos.diner.delivery.model.Order;
import ru.commandos.diner.delivery.view.MainActivity;

@EverythingIsNonNull
public class OrderNotificationController {

    public static final int NOTIFICATION_ID = 1;
    private final MainActivity mainActivity;
    private final Context context;

    public OrderNotificationController(MainActivity mainActivity, Context context) {
        this.mainActivity = mainActivity;
        this.context = context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannelDelivery();
        }
    }

    public void showIncomingOrderNotification(Order order) {
        NotificationManager notificationManager =
                (NotificationManager) mainActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent(context, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, "DELIVERY_CHANNEL_ID")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("У вас новый заказ!")
                        .setContentText(order.getReadableFeatures() + " " + order.getReadableMass())
                        .setContentIntent(resultPendingIntent);
        Notification notification = builder.build();
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    public void showIncomingOrder(Order order) {
        if (order != null) {
            showIncomingOrderNotification(order);
        } else {
            deleteNotification();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNotificationChannelDelivery() {
        NotificationManager notificationManager =
                (NotificationManager) mainActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("DELIVERY_CHANNEL_ID", "Delivery",
                NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Notifications about orders");
        channel.enableLights(true);
        channel.setLightColor(Color.RED);
        channel.enableVibration(false);
        notificationManager.createNotificationChannel(channel);
    }

    public void deleteNotification() {
        NotificationManager notificationManager =
                (NotificationManager) mainActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }
}
