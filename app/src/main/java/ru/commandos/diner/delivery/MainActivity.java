package ru.commandos.diner.delivery;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

import ru.commandos.diner.delivery.controller.OrderAcceptingController;
import ru.commandos.diner.delivery.databinding.ActivityMainBinding;
import ru.commandos.diner.delivery.model.Feature;
import ru.commandos.diner.delivery.model.Item;
import ru.commandos.diner.delivery.model.Order;

public class MainActivity extends AppCompatActivity {

    public ActivityMainBinding binding;
    public AcceptedOrdersAdapter adapter;

    public ArrayList<Order> orders = new ArrayList<>();
    public Order currentOrder;

    public OrderAcceptingController controller = new OrderAcceptingController();

    {
        Order order = new Order(UUID.randomUUID());
        Feature[] f = new Feature[2];
        f[0] = Feature.LIQUID;
        f[1] = Feature.SHOULD_BE_COLD;
        Item item = new Item("Пельмеши", (float) 5.15, f);
        order.items.add(item);
        order.items.add(item);
        orders.add(order);
        orders.add(order);
        orders.add(order);

        currentOrder = order;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.recyclerViewAcceptedOrders.setHasFixedSize(true);
        binding.recyclerViewAcceptedOrders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AcceptedOrdersAdapter(this, orders);
        binding.recyclerViewAcceptedOrders.setAdapter(adapter);

        createNotificationChannelDelivery();

        binding.textViewCurrentUUID.setText(currentOrder.uuid.toString());
        binding.textViewCurrentFood.setText(getActualStringFood(currentOrder));

        binding.buttonAccept.setOnClickListener(new ButtonAcceptOnClickListener());
        binding.buttonDeny.setOnClickListener(new ButtonDenyOnClickListener());

        binding.textViewCurrentFeatures.setText(getActualStringFeatures(currentOrder));
        binding.textViewCurrentMass.setText(getActualStringMass(currentOrder));
    }

    @Override
    protected void onPause() {
        super.onPause();
        showNotificationAboutCurrentOrder();
    }

    public void showNotificationAboutCurrentOrder() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, "CHANNEL_ID")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("У вас новый заказ!")
                        .setContentText(getActualStringFood(currentOrder))
                        .setContentIntent(resultPendingIntent);

        Notification notification = builder.build();
        notificationManager.notify(1, notification);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNotificationChannelDelivery() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel("CHANNEL_ID", "Delivery",
                NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Notifications about orders");
        channel.enableLights(true);
        channel.setLightColor(Color.RED);
        channel.enableVibration(false);
        notificationManager.createNotificationChannel(channel);
    }

    public static String getActualStringFood(Order order) {
        StringBuilder food = new StringBuilder(order.items.get(0).name);
        for (int i = 1; i < order.items.size(); i++) {
            food.append(", ");
            String c = order.items.get(i).name;
            c = c.toLowerCase();
            food.append(c);
        }
        return String.valueOf(food);
    }

    public static String getActualStringFeatures(Order order) {
        String features = "";
        HashSet<Feature> h = new HashSet<>(3);
        for (int i = 0; i < order.items.size(); i++)
            Collections.addAll(h, order.items.get(i).features);
        if (h.contains(Feature.LIQUID)) features = "Есть жидкости";
        if (h.contains(Feature.SHOULD_BE_COLD))
            if (features.equals("")) features = "Должно быть холодным";
            else features += ", должно быть холодным";
        if (h.contains(Feature.SHOULD_BE_HOT))
            if (features.equals("")) features = "";
            else features += ", должно быть горячим";
        features += "!";
        return features;
    }

    public static String getActualStringMass(Order order) {
        float m = 0;
        for (int i = 0; i < order.items.size(); i++) {
            m += order.items.get(i).mass;
        }
        return String.valueOf(m);
    }

    public class ButtonAcceptOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            controller.acceptAcceptableOrder(currentOrder);
        }
    }

    public class ButtonDenyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            controller.denyAcceptableOrder();
        }
    }
}