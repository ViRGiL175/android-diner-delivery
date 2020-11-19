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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import ru.commandos.diner.delivery.controller.OrdersController;
import ru.commandos.diner.delivery.databinding.ActivityMainBinding;
import ru.commandos.diner.delivery.model.Feature;
import ru.commandos.diner.delivery.model.Order;

public class MainActivity extends AppCompatActivity {

    public static final String nameForSharedPreferencesHelperOrder = "currentOrder";
    public static final String nameForSharedPreferencesHelperOrders = "orders";
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    public ActivityMainBinding binding;
    public AcceptedOrdersAdapter adapter;
    public ArrayList<Order> orders;
    public Order incomingOrder;
    public OrdersController controller;
    public SharedPreferencesHelper<Order> sharedPreferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPreferencesHelper = new SharedPreferencesHelper<>(this, Order.class);

        orders = sharedPreferencesHelper.getModelsArrayList(nameForSharedPreferencesHelperOrders);
        if (orders == null) {
            orders = new ArrayList<>();
        }

        incomingOrder = sharedPreferencesHelper.getModel(nameForSharedPreferencesHelperOrder);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        controller = new OrdersController("df307a18-1b66-432a-8011-39b68397d000", this);
        controller.startChecking();

        binding.recyclerViewAcceptedOrders.setHasFixedSize(true);
        binding.recyclerViewAcceptedOrders.setLayoutManager(new LinearLayoutManager(this));

        incomingOrder = controller.getIncomingOrder();

        adapter = new AcceptedOrdersAdapter(this, orders);
        binding.recyclerViewAcceptedOrders.setAdapter(adapter);

        binding.buttonAccept.setOnClickListener(new ButtonAcceptOnClickListener());
        binding.buttonDeny.setOnClickListener(new ButtonDenyOnClickListener());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannelDelivery();
        }
        updateView();
        makeButtonsDisabled();
        collapseInfoAboutCurrentOrder();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferencesHelper.saveModelsArrayList(nameForSharedPreferencesHelperOrders, orders);
        sharedPreferencesHelper.saveModel(nameForSharedPreferencesHelperOrder, incomingOrder);
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

    public void updateView() {
        incomingOrder = controller.getIncomingOrder();
        if (incomingOrder == null) {
            makeButtonsDisabled();
        } else {
            String mass = getActualStringMass(incomingOrder) + " кг";
            runOnUiThread(() -> {
                binding.textViewCurrentUUID.setText(incomingOrder.getUuid());
                binding.textViewCurrentFood.setText(getActualStringFood(incomingOrder));
                binding.textViewCurrentFeatures.setText(getActualStringFeatures(incomingOrder));
                binding.textViewCurrentMass.setText(mass);

                binding.textViewContent.setText("Содержимое: ");
                binding.textViewOrder.setText("Заказ");
                binding.textViewTotalWeight.setText("Общий вес: ");
                binding.textViewWarn.setText("Новый заказ!");
            });
        }

        orders = sharedPreferencesHelper.getModelsArrayList(nameForSharedPreferencesHelperOrders);

        ArrayList<Order> orders1;
        orders1 = controller.getAcceptOrderList();

        if (orders1 == null) {
            orders1 = new ArrayList<>();
        }

        orders.addAll(orders1);

        if (orders == null) {
            orders = new ArrayList<>();
        }

        if (orders.size() > 0) {
            AcceptedOrdersAdapter adapter1 = new AcceptedOrdersAdapter(this, orders);
            runOnUiThread(() -> binding.recyclerViewAcceptedOrders.setAdapter(adapter1));
        }

        runOnUiThread(() -> {
            binding.buttonDeny.setEnabled(true);
            binding.buttonAccept.setEnabled(true);
        });
        showNotificationAboutCurrentOrder();
    }

    public void makeButtonsDisabled() {
        binding.buttonAccept.setEnabled(false);
        binding.buttonDeny.setEnabled(false);
    }

    public void collapseInfoAboutCurrentOrder() {
        runOnUiThread(() -> {
            binding.textViewWarn.setText("У вас нет новых заказов");
            binding.textViewOrder.setText("");
            binding.textViewTotalWeight.setText("");
            binding.textViewContent.setText("");
            binding.textViewCurrentUUID.setText("");
            binding.textViewCurrentFood.setText("");
            binding.textViewCurrentFeatures.setText("");
            binding.textViewCurrentMass.setText("");
        });
    }

    public String getActualStringMass(Order order) {
//        if (order.items != null) {
            return String.valueOf(order.getItems().stream().mapToLong(value ->
                    (long) value.getMass()).sum());
//        } else {
//            return "NULL";
//        }
    }

    public String getActualStringFood(Order order) {
//        if (order.items != null) {
            StringBuilder food = new StringBuilder(order.getItems().get(0).getName());
            for (int i = 1; i < order.getItems().size(); i++) {
                food.append(", ");
                String c = order.getItems().get(i).getName();
                c = c.toLowerCase();
                food.append(c);
            }
            return String.valueOf(food);
//        } else {
//            return "NULL";
//        }
    }

    public String getActualStringFeatures(Order order) {
        String features = "";
        HashSet<Feature> h = new HashSet<>(3);
//        if (order.items != null) {
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
//        } else {
//            return "NULL";
//        }
    }

    public void showNotificationAboutCurrentOrder() {
        if (incomingOrder != null) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            Intent resultIntent = new Intent(this, MainActivity.class);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this, "DELIVERY_CHANNEL_ID")
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("У вас новый заказ!")
                            .setContentText(getActualStringFeatures(incomingOrder) + " " + getActualStringMass(incomingOrder) + " кг")
                            .setContentIntent(resultPendingIntent);

            Notification notification = builder.build();
            notificationManager.notify(1, notification);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sharedPreferencesHelper.saveModelsArrayList(nameForSharedPreferencesHelperOrders, orders);
        sharedPreferencesHelper.saveModel(nameForSharedPreferencesHelperOrder, incomingOrder);
    }

    @Override
    protected void onResume() {
        super.onResume();
        orders = controller.getAcceptOrderList();
        if (orders == null) {
            orders = new ArrayList<>();
        }

        incomingOrder = controller.getIncomingOrder();
        updateView();
    }

    public void deleteNotification() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
    }

    public class ButtonAcceptOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (controller.getIncomingOrder() != null) {
                controller.acceptOrder();
                updateView();
            }
            makeButtonsDisabled();
            collapseInfoAboutCurrentOrder();
            deleteNotification();
        }
    }

    public class ButtonDenyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            controller.denyOrder();
            updateView();
            makeButtonsDisabled();
            collapseInfoAboutCurrentOrder();
            deleteNotification();
        }
    }
}
