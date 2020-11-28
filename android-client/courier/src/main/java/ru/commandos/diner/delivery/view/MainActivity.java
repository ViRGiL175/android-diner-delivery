package ru.commandos.diner.delivery.view;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.jakewharton.rxbinding4.view.RxView;
import com.rey.material.widget.Switch;

import autodispose2.AutoDispose;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
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
        binding.cardView.enterToOfflineMode();

        orderNotificationController = new OrderNotificationController(this, this);

        RxView.clicks(binding.cardView.getBinding().incomingAcceptButton)
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(this::onAcceptClick);
        RxView.clicks(binding.cardView.getBinding().incomingDenyButton)
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(this::onDenyClick);
        RxView.clicks(binding.switchOflline)
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(this::onSwitchPositionChanged);
        ordersController.getIncomingOrderObservable()
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(order -> binding.cardView.showIncomingOrder(order));
        ordersController.getIncomingOrderObservable()
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(order -> orderNotificationController.showIncomingOrder(order));
        ordersController.getAcceptedOrdersObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(orders -> binding.recyclerView.getAdapter().notifyDataSetChanged());
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

    private void onSwitchPositionChanged(Unit unit) {
        if (binding.switchOflline.isChecked()) {
            ordersController.enterToOfflineMode();
            binding.cardView.enterToOfflineMode();
        } else {
            ordersController.exitFromOfflineMode();
            binding.cardView.exitFromOfflineMode();
            orderNotificationController.deleteNotification();
        }
    }
}
