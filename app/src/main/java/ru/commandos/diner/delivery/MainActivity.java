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

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jakewharton.rxbinding4.view.RxView;

import java.util.Collections;
import java.util.HashSet;

import autodispose2.AutoDispose;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import kotlin.Unit;
import ru.commandos.diner.delivery.controller.OrdersController;
import ru.commandos.diner.delivery.databinding.ActivityMainBinding;
import ru.commandos.diner.delivery.model.Feature;
import ru.commandos.diner.delivery.model.Order;

public class MainActivity extends AppCompatActivity {

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    public ActivityMainBinding binding;
    public AcceptedOrdersAdapter adapter;
    public OrdersController ordersController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ordersController = new OrdersController("df307a18-1b66-432a-8011-39b68397d000", this);

        setupRecyclerView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannelDelivery();
        }

        RxView.clicks(binding.acceptButton)
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(this::onAcceptClick);
        RxView.clicks(binding.denyButton)
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(this::onDenyClick);
        ordersController.getOrderObservable()
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(this::showIncomingOrder);
    }

    public void setIncomingOrderViewsVisibility(boolean visibility) {
        runOnUiThread(() -> {
            binding.acceptButton.setVisibility(getVisibility(visibility));
            binding.denyButton.setVisibility(getVisibility(visibility));
            binding.uuid.setVisibility(getVisibility(visibility));
            binding.uuidLabel.setVisibility(getVisibility(visibility));
            binding.food.setVisibility(getVisibility(visibility));
            binding.contentLabel.setVisibility(getVisibility(visibility));
            binding.features.setVisibility(getVisibility(visibility));
            binding.mass.setVisibility(getVisibility(visibility));
            binding.massLabel.setVisibility(getVisibility(visibility));
        });
    }

    private int getVisibility(boolean visibility) {
        return visibility ? View.VISIBLE : View.GONE;
    }

    private void showIncomingOrder(@Nullable Order order) {
        setIncomingOrderViewsVisibility(order != null);
        if (order != null) {
            showIncomingOrderNotification(order);
            runOnUiThread(() -> {
                binding.newOrderWarning.setText("Новый заказ!");
                binding.uuid.setText(order.getUuid());
                binding.food.setText(getReadableContent(order));
                binding.mass.setText(getReadableMass(order));
                binding.features.setText(getRadableFeatures(order));
            });
        } else {
            runOnUiThread(() -> binding.newOrderWarning.setText("Ожидайте новый заказ...")
            );
            deleteNotification();
        }
    }

    private void setupRecyclerView() {
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AcceptedOrdersAdapter(this, ordersController.getAcceptedOrders());
        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ordersController.onDestroy();
        compositeDisposable.dispose();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNotificationChannelDelivery() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("DELIVERY_CHANNEL_ID", "Delivery",
                NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Notifications about orders");
        channel.enableLights(true);
        channel.setLightColor(Color.RED);
        channel.enableVibration(false);
        notificationManager.createNotificationChannel(channel);
    }

    public String getReadableMass(Order order) {
        return order.getItems().stream().mapToLong(value ->
                (long) value.getMass()).sum() + " кг";
    }

    public String getReadableContent(Order order) {
        StringBuilder food = new StringBuilder(order.getItems().get(0).getName());
        for (int i = 1; i < order.getItems().size(); i++) {
            food.append(", ");
            String c = order.getItems().get(i).getName();
            c = c.toLowerCase();
            food.append(c);
        }
        return String.valueOf(food);
    }

    public String getRadableFeatures(Order order) {
        String features = "";
        HashSet<Feature> h = new HashSet<>(3);
        for (int i = 0; i < order.getItems().size(); i++)
            Collections.addAll(h, order.getItems().get(i).getFeatures());
        if (h.contains(Feature.LIQUID)) features = "Есть жидкости";
        if (h.contains(Feature.SHOULD_BE_COLD))
            if (features.equals("")) features = "Должно быть холодным";
            else features += ", должно быть холодным";
        if (h.contains(Feature.SHOULD_BE_HOT))
            if (features.equals("")) features = "";
            else features += ", должно быть горячим";
        if (features.equals(""))
            return "Нет особенностей";
        else
            return features += "!";
    }

    public void showIncomingOrderNotification(Order order) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, "DELIVERY_CHANNEL_ID")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("У вас новый заказ!")
                        .setContentText(getRadableFeatures(order) + " " + getReadableMass(order) + " кг")
                        .setContentIntent(resultPendingIntent);

        Notification notification = builder.build();
        notificationManager.notify(1, notification);
    }

    public void deleteNotification() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
    }

    private void onDenyClick(Unit unit) {
        ordersController.denyOrder();
        showIncomingOrder(null);
    }

    private void onAcceptClick(Unit unit) {
        ordersController.acceptOrder();
        adapter.notifyDataSetChanged();
        showIncomingOrder(null);
    }
}
