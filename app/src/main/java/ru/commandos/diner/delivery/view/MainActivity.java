package ru.commandos.diner.delivery.view;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.jakewharton.rxbinding4.view.RxView;

import java.util.List;

import autodispose2.AutoDispose;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import kotlin.Unit;
import ru.commandos.diner.delivery.controller.OrderNotificationController;
import ru.commandos.diner.delivery.controller.OrdersController;
import ru.commandos.diner.delivery.databinding.MainActivityBinding;
import ru.commandos.diner.delivery.model.Order;

public class MainActivity extends AppCompatActivity {

    private OrdersController ordersController;
    private OrderNotificationController orderNotificationController;
    private MainActivityBinding binding;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Observable<Order> incomingOrderObservable;

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
        ordersController.getAcceptedOrdersObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(orders -> binding.recyclerView.getAdapter().notifyDataSetChanged());
        incomingOrderObservable = ordersController.getIncomingOrderObservable();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
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
            compositeDisposable.dispose();
            binding.cardView.enterToOfflineMode();
        } else {
            binding.cardView.exitFromOfflineMode();
            orderNotificationController.deleteNotification();
            updateSubscribes();
        }
    }

    @SuppressLint("AutoDispose")
    private void updateSubscribes() {
        compositeDisposable.add(incomingOrderObservable
                .subscribe(order -> binding.cardView.showIncomingOrder(order)));
        compositeDisposable.add(incomingOrderObservable
                .subscribe(order -> orderNotificationController.showIncomingOrder(order)));
    }
}
