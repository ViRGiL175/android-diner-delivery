package ru.commandos.diner.delivery.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.jakewharton.rxbinding4.view.RxView;
import com.jakewharton.rxbinding4.widget.RxCompoundButton;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import autodispose2.AutoDispose;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import kotlin.Unit;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import ru.commandos.diner.delivery.R;
import ru.commandos.diner.delivery.controller.LocationController;
import ru.commandos.diner.delivery.controller.OrderNotificationController;
import ru.commandos.diner.delivery.controller.OrdersController;
import ru.commandos.diner.delivery.databinding.MainActivityBinding;
import ru.commandos.diner.delivery.model.Order;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {

    private final CompositeDisposable offlineDisposables = new CompositeDisposable();
    private LocationController locationController;
    private OrdersController ordersController;
    private OrderNotificationController orderNotificationController;
    private MainActivityBinding binding;
    private Observable<Order> incomingOrderObservable;
    private String readyToHandshakeOrderUUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MainActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ordersController = new OrdersController("df307a18-1b66-432a-8011-39b68397d000", this);

        orderNotificationController = new OrderNotificationController(this, this);
        incomingOrderObservable = ordersController.getIncomingOrderObservable();

        locationController = new LocationController(this);
        MainActivityPermissionsDispatcher.setupLocationWithPermissionCheck(this);

        RxView.clicks(binding.backdrop.cardView.getBinding().incomingAcceptButton)
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(this::onAcceptClick);
        RxView.clicks(binding.backdrop.cardView.getBinding().incomingDenyButton)
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(this::onDenyClick);
        RxView.clicks(binding.showAll)
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(unit -> locationController.showAllOnMap());
        RxCompoundButton.checkedChanges(binding.backdrop.switchOffline)
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(this::onOfflineModeChecked);
        ordersController.getAcceptedOrdersObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(orders -> {
                    binding.backdrop.recyclerView.getAdapter().notifyDataSetChanged();
                    locationController.updateAcceptedOrders();
                });

        Completable.timer(10, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(() -> {
                    Toast.makeText(this, "Курьер подошел к клиенту", Toast.LENGTH_LONG).show();
                    ordersController.getAcceptedOrders().get(0).setResolvable(true);
                    binding.backdrop.recyclerView.getAdapter().notifyDataSetChanged();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null && readyToHandshakeOrderUUID != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                try {
                    ordersController.deliveryOrder(readyToHandshakeOrderUUID, result.getContents());
                    Toast.makeText(this, "Успешно", Toast.LENGTH_LONG).show();
                } catch (Throwable throwable) {
                    Toast.makeText(this, "QR код неверен", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
        readyToHandshakeOrderUUID = null;
    }

    @NeedsPermission({ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION})
    protected void setupLocation() {
        locationController.setAcceptedOrders(ordersController.getAcceptedOrders());
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(locationController);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    public void setReadyToHandshakeOrderUUID(String uuid) {
        readyToHandshakeOrderUUID = uuid;
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
        binding.backdrop.recyclerView.setOrders(ordersController.getAcceptedOrders());
        binding.backdrop.switchOffline.setChecked(ordersController.getOfflineState());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        offlineDisposables.dispose();
    }

    private void onDenyClick(Unit unit) {
        ordersController.denyOrder();
        binding.backdrop.cardView.showIncomingOrder(null);
        orderNotificationController.deleteNotification();
    }

    private void onAcceptClick(Unit unit) {
        ordersController.acceptOrder();
        binding.backdrop.recyclerView.getAdapter().notifyDataSetChanged();
        binding.backdrop.cardView.showIncomingOrder(null);
        orderNotificationController.deleteNotification();
    }

    @SuppressLint("AutoDispose")
    private void onOfflineModeChecked(@NotNull Boolean checked) {
        binding.backdrop.cardView.onOfflineModeChecked(checked);
        ordersController.setOfflineState(checked);
        orderNotificationController.deleteNotification();
        if (checked) {
            offlineDisposables.clear();
            locationController.showIncomingOrder(null);
        } else {
            offlineDisposables.add(incomingOrderObservable.observeOn(AndroidSchedulers.mainThread())
                    .subscribe(order -> {
                        binding.backdrop.cardView.showIncomingOrder(order);
                        orderNotificationController.showIncomingOrder(order);
                        locationController.showIncomingOrder(order);
                    }));
        }
    }
}
