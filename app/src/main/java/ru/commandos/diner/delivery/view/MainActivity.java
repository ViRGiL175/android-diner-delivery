package ru.commandos.diner.delivery.view;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.jakewharton.rxbinding4.view.RxView;

import autodispose2.AutoDispose;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import kotlin.Unit;
import ru.commandos.diner.delivery.controller.OrderNotificationController;
import ru.commandos.diner.delivery.controller.OrdersController;
import ru.commandos.diner.delivery.databinding.MainActivityBinding;

public class MainActivity extends AppCompatActivity {

    OrdersController ordersController;
    OrderNotificationController orderNotificationController;
    MainActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MainActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ordersController = new OrdersController("df307a18-1b66-432a-8011-39b68397d000", this);
        binding.recyclerView.setOrders(ordersController.getAcceptedOrders());

        orderNotificationController = new OrderNotificationController(this, this);

        RxView.clicks(binding.cardView.getBinding().incomingAcceptButton)
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(this::onAcceptClick);
        RxView.clicks(binding.cardView.getBinding().incomingDenyButton)
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(this::onDenyClick);
        ordersController.getIncomingOrderObservable()
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(response -> binding.cardView.showIncomingOrder(response.body()));
        ordersController.getIncomingOrderObservable()
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(response -> orderNotificationController.showIncomingOrder(response.body()));
    }

    @Override
    protected void onPause() {
        super.onPause();
        ordersController.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ordersController.onResume();
    }

    private void onDenyClick(Unit unit) {
        ordersController.denyOrder();
        binding.cardView.showIncomingOrder(null);
        orderNotificationController.deleteNotification();
    }

    private void onAcceptClick(Unit unit) {
        ordersController.acceptOrder();
        binding.recyclerView.getAdapter().notifyDataSetChanged();
        binding.cardView.showIncomingOrder(null);
        orderNotificationController.deleteNotification();
    }
}
