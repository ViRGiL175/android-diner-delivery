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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import ru.commandos.diner.delivery.controller.OrderAcceptingController;
import ru.commandos.diner.delivery.databinding.ActivityMainBinding;
import ru.commandos.diner.delivery.model.Feature;
import ru.commandos.diner.delivery.model.Order;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class MainActivity extends AppCompatActivity {

    public ActivityMainBinding binding;
    public AcceptedOrdersAdapter adapter;
    public ArrayList<Order> orders;
    public Order currentOrder;
    public OrderAcceptingController controller;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        controller = new OrderAcceptingController("df307a18-1b66-432a-8011-39b68397d000", compositeDisposable, this);
        controller.check();

        binding.recyclerViewAcceptedOrders.setHasFixedSize(true);
        binding.recyclerViewAcceptedOrders.setLayoutManager(new LinearLayoutManager(this));

//        for(int i=0;i<orders.size();i++) {
//            if(orders.get(i) == null) {
//                orders.remove(i);
//            }
//        }

        orders = controller.getAcceptOrderList();
        if(orders == null) {
            orders = new ArrayList<>();
        }

        currentOrder = controller.getAcceptableOrder();

        adapter = new AcceptedOrdersAdapter(this, orders);
        binding.recyclerViewAcceptedOrders.setAdapter(adapter);

        binding.buttonAccept.setOnClickListener(new ButtonAcceptOnClickListener());
        binding.buttonDeny.setOnClickListener(new ButtonDenyOnClickListener());

        createNotificationChannelDelivery();
        updateView();
        makeButtonsDisabled();
        collapseInfoAboutCurrentOrder();
    }

    @Override
    protected void onResume() {
        super.onResume();
        orders = controller.getAcceptOrderList();
        if(orders == null) {
            orders = new ArrayList<>();
        }

        currentOrder = controller.getAcceptableOrder();
        updateView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    public void updateView() {
        currentOrder = controller.getAcceptableOrder();
        if(currentOrder == null) {
            makeButtonsDisabled();
        }
        else {
            String mass = getActualStringMass(currentOrder) + " кг";
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    binding.textViewCurrentUUID.setText(currentOrder.uuid.toString());
                    binding.textViewCurrentFood.setText(getActualStringFood(currentOrder));
                    binding.textViewCurrentFeatures.setText(getActualStringFeatures(currentOrder));
                    binding.textViewCurrentMass.setText(mass);

                    binding.textViewContent.setText("Содержимое: ");
                    binding.textViewOrder.setText("Заказ");
                    binding.textViewTotalWeight.setText("Общий вес: ");
                    binding.textViewWarn.setText("У вас новый заказ!");
                }
            });
        }

        orders = controller.getAcceptOrderList();
        if (orders == null) {
            orders = new ArrayList<>();
        }

//        for(int i=0;i<orders.size();i++) {
//            if(orders.get(i) == null) {
//                orders.remove(i);
//            }
//        }

        if(orders.size()>0)
        {
            AcceptedOrdersAdapter adapter1 = new AcceptedOrdersAdapter(this, orders);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    binding.recyclerViewAcceptedOrders.setAdapter(adapter1);
//                    adapter.notifyDataSetChanged();
                }
            });
        }

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                binding.buttonDeny.setEnabled(true);
                binding.buttonAccept.setEnabled(true);
            }
        });
        showNotificationAboutCurrentOrder();
    }

    public void showNotificationAboutCurrentOrder() {
        if (currentOrder != null) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            Intent resultIntent = new Intent(this, MainActivity.class);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this, "DELIVERY_CHANNEL_ID")
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("У вас новый заказ!")
                            .setContentText(getActualStringFeatures(currentOrder) + " " + getActualStringMass(currentOrder) + " кг")
                            .setContentIntent(resultPendingIntent);

            Notification notification = builder.build();
            notificationManager.notify(1, notification);
        }
    }

    public void deleteNotification() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
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
            if(controller.getAcceptableOrder()!=null) {
                controller.acceptAcceptableOrder();
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
            controller.denyAcceptableOrder();
            updateView();
            makeButtonsDisabled();
            collapseInfoAboutCurrentOrder();
            deleteNotification();
        }
    }

    public void makeButtonsDisabled() {
        binding.buttonAccept.setEnabled(false);
        binding.buttonDeny.setEnabled(false);
    }

    public void collapseInfoAboutCurrentOrder() {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                binding.textViewWarn.setText("У вас нет новых заказов");
                binding.textViewOrder.setText("");
                binding.textViewTotalWeight.setText("");
                binding.textViewContent.setText("");
                binding.textViewCurrentUUID.setText("");
                binding.textViewCurrentFood.setText("");
                binding.textViewCurrentFeatures.setText("");
                binding.textViewCurrentMass.setText("");
            }
        });
    }
}