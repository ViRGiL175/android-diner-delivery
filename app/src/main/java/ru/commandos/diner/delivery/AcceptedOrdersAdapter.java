package ru.commandos.diner.delivery;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.commandos.diner.delivery.databinding.AcceptedOrderBinding;
import ru.commandos.diner.delivery.model.Order;

public class AcceptedOrdersAdapter extends RecyclerView.Adapter<AcceptedOrderHolder> {

    private final ArrayList<Order> orders;
    private final MainActivity activity;

    public AcceptedOrdersAdapter(MainActivity activity, ArrayList<Order> orders) {
        this.orders = orders;
        this.activity = activity;
    }

    @NonNull
    @Override
    public AcceptedOrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        AcceptedOrderBinding binding = AcceptedOrderBinding.inflate(layoutInflater, parent, false);
        return new AcceptedOrderHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AcceptedOrderHolder holder, int position) {
        Order order = orders.get(position);
        holder.binding.foodItems.setText(activity.getReadableContent(order));
        holder.binding.orderUuid.setText(order.getUuid());
        holder.binding.foodFeatures.setText(activity.getRadableFeatures(order));
        holder.binding.foodMass.setText(String.format("%s кг", activity.getReadableMass(order)));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }
}
