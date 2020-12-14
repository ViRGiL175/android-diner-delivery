package ru.commandos.diner.delivery.view;

import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.jakewharton.rxbinding4.view.RxView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import autodispose2.AutoDispose;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import io.reactivex.rxjava3.subjects.CompletableSubject;
import io.reactivex.rxjava3.subjects.PublishSubject;
import retrofit2.internal.EverythingIsNonNull;
import ru.commandos.diner.delivery.controller.ActivityGetter;
import ru.commandos.diner.delivery.databinding.AcceptedOrderBinding;
import ru.commandos.diner.delivery.model.Order;

@EverythingIsNonNull
public class AcceptedOrdersAdapter extends RecyclerView.Adapter<AcceptedOrdersAdapter
        .AcceptedOrderHolder> {

    private final CompletableSubject adapterDisposing = CompletableSubject.create();
    private final PublishSubject<Order> orderResolved = PublishSubject.create();
    private List<Order> orders = new ArrayList<>();
    private Map<Order, Boolean> ordersResolving = new HashMap<>();

    @NonNull
    @Override
    public AcceptedOrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = ActivityGetter.getActivity(parent)
                .orElseThrow(InflateException::new).getLayoutInflater();
        AcceptedOrderBinding binding = AcceptedOrderBinding.inflate(layoutInflater, parent, false);
        return new AcceptedOrderHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AcceptedOrderHolder holder, int position) {
        Order order = orders.get(position);
        holder.binding.acceptedContent.setText(order.getReadableContent());
        holder.binding.acceptedUuid.setText(order.getUuid());
        holder.binding.acceptedFeatures.setText(order.getReadableFeatures());
        holder.binding.acceptedMass.setText(order.getReadableMass());
        holder.binding.resolve.setVisibility(Optional.ofNullable(ordersResolving.get(order))
                .orElse(false) ? View.VISIBLE : View.GONE);
        Optional<AppCompatActivity> optional = ActivityGetter.getActivity(holder.itemView);
        if (optional.isPresent() && optional.get() instanceof MainActivity) {
            MainActivity activity = (MainActivity) optional.get();
            RxView.clicks(holder.binding.resolve)
                    .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(activity)))
                    .subscribe(unit -> scanQRCode(activity, order));
        }
    }

    private void scanQRCode(MainActivity activity, Order order) {
        activity.setReadyToHandshakeOrderUUID(order.getUuid());
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan a QR code");
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        adapterDisposing.onComplete();
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public void setOrdersResolving(Map<Order, Boolean> ordersResolving) {
        this.ordersResolving = ordersResolving;
    }

    @EverythingIsNonNull
    static class AcceptedOrderHolder extends RecyclerView.ViewHolder {

        public AcceptedOrderBinding binding;

        public AcceptedOrderHolder(AcceptedOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
