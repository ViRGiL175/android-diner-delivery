package ru.commandos.diner.delivery.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.internal.EverythingIsNonNull;
import ru.commandos.diner.delivery.controller.ActivityGetter;
import ru.commandos.diner.delivery.databinding.AcceptedOrderBinding;
import ru.commandos.diner.delivery.model.Order;

public class AcceptedOrdersAdapter extends RecyclerView.Adapter<
        AcceptedOrdersAdapter.AcceptedOrderHolder> {

    private List<Order> orders = new ArrayList<>();

    @NonNull
    @Override
    public AcceptedOrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = ActivityGetter.getActivity(parent).getLayoutInflater();
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
