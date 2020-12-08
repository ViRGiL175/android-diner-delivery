package ru.commandos.diner.delivery.view;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.jakewharton.rxbinding4.view.RxView;
import com.jakewharton.rxbinding4.widget.RxCompoundButton;

import org.jetbrains.annotations.NotNull;

import autodispose2.AutoDispose;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import kotlin.Unit;
import ru.commandos.diner.delivery.controller.OrderNotificationController;
import ru.commandos.diner.delivery.controller.OrdersController;
import ru.commandos.diner.delivery.databinding.MainActivityBinding;
import ru.commandos.diner.delivery.model.Order;

public class MainActivity extends AppCompatActivity {

    private final CompositeDisposable offlineDisposables = new CompositeDisposable();
    private OrdersController ordersController;
    private OrderNotificationController orderNotificationController;
    private MainActivityBinding binding;
    private Observable<Order> incomingOrderObservable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MainActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ordersController = new OrdersController("df307a18-1b66-432a-8011-39b68397d000", this);

        orderNotificationController = new OrderNotificationController(this, this);
        incomingOrderObservable = ordersController.getIncomingOrderObservable();

        RxView.clicks(binding.cardView.getBinding().incomingAcceptButton)
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(this::onAcceptClick);
        RxView.clicks(binding.cardView.getBinding().incomingDenyButton)
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(this::onDenyClick);
        RxCompoundButton.checkedChanges(binding.switchOffline)
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(this::onOfflineModeChecked);
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
        binding.recyclerView.setOrders(ordersController.getAcceptedOrders());
        binding.switchOffline.setChecked(ordersController.getOfflineState());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        offlineDisposables.dispose();
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

    @SuppressLint("AutoDispose")
    private void onOfflineModeChecked(@NotNull Boolean checked) {
        binding.cardView.onOfflineModeChecked(checked);
        ordersController.setOfflineState(checked);
        orderNotificationController.deleteNotification();
        if (checked) {
            offlineDisposables.clear();
        } else {
            offlineDisposables.add(incomingOrderObservable.subscribe(order -> {
                binding.cardView.showIncomingOrder(order);
                orderNotificationController.showIncomingOrder(order);
            }));
        }
    }
}
