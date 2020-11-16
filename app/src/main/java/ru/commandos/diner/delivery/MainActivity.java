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
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.UUID;

import ru.commandos.diner.delivery.databinding.ActivityMainBinding;
import ru.commandos.diner.delivery.model.Feature;
import ru.commandos.diner.delivery.model.Item;
import ru.commandos.diner.delivery.model.Order;

public class MainActivity extends AppCompatActivity {

    public ActivityMainBinding binding;
    public AcceptedOrdersAdapter adapter;

    public ArrayList<Order> orders = new ArrayList<>();
    public Order currentOrder;

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
    }
}