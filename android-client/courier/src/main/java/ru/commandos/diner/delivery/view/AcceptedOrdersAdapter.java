package ru.commandos.diner.delivery.view;

import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxbinding4.view.RxView;

import java.util.ArrayList;
import java.util.List;

import autodispose2.AutoDispose;
import io.reactivex.rxjava3.subjects.CompletableSubject;
import io.reactivex.rxjava3.subjects.PublishSubject;
import retrofit2.internal.EverythingIsNonNull;
import ru.commandos.diner.delivery.controller.ActivityGetter;
import ru.commandos.diner.delivery.databinding.AcceptedOrderBinding;
import ru.commandos.diner.delivery.model.ApiOrder;
import ru.commandos.diner.delivery.model.Order;

@EverythingIsNonNull
public class AcceptedOrdersAdapter extends RecyclerView.Adapter<AcceptedOrdersAdapter
        .AcceptedOrderHolder> {

    private final CompletableSubject adapterDisposing = CompletableSubject.create();
    private final PublishSubject<ApiOrder> orderResolved = PublishSubject.create();
    private List<Order> orders = new ArrayList<>();

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
        holder.binding.resolve.setVisibility(order.isResolvable() ? View.VISIBLE : View.GONE);
        RxView.clicks(holder.binding.resolve)
                .to(AutoDispose.autoDisposable(observer -> adapterDisposing
                        .doOnComplete(observer::onComplete)
                        .subscribe()))
                .subscribe(unit -> orderResolved.onNext(order));
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

    @EverythingIsNonNull
    static class AcceptedOrderHolder extends RecyclerView.ViewHolder {

        public AcceptedOrderBinding binding;

        public AcceptedOrderHolder(AcceptedOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
