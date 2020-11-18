package ru.commandos.diner.delivery;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.commandos.diner.delivery.databinding.AcceptedOrderBinding;
import ru.commandos.diner.delivery.model.Order;

public class AcceptedOrdersAdapter extends RecyclerView.Adapter<AcceptedOrderHolder> {
    ArrayList<Order> orders;
    Activity activity;

    public AcceptedOrdersAdapter(Activity activity, ArrayList<Order> orders) {
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
        String uuid = order.uuid;

        holder.binding.textViewFood.setText(MainActivity.getActualStringFood(order));
        holder.binding.textViewUUID.setText(uuid);
        holder.binding.textViewFeatures.setText(MainActivity.getActualStringFeatures(order));
        holder.binding.textViewMass.setText(MainActivity.getActualStringMass(order) + " кг");
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }
}
