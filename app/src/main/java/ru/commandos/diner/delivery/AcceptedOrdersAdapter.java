package ru.commandos.diner.delivery;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import ru.commandos.diner.delivery.databinding.AcceptedOrderBinding;
import ru.commandos.diner.delivery.model.Feature;
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
        String uuid = order.uuid.toString();

        String features = "";
        HashSet<Feature> h = new HashSet<>(3);
        for (int i = 0; i < order.items.size(); i++)
            Collections.addAll(h, order.items.get(i).features);
        if (h.contains(Feature.LIQUID)) features = "Есть жидкости";
        if (h.contains(Feature.SHOULD_BE_COLD))
            if (features.equals("")) features = "Должно быть холодным";
            else features += " , должно быть холодным";
        if (h.contains(Feature.SHOULD_BE_HOT))
            if (features.equals("")) features = "";
            else features += " , должно быть горячим";
        features += "!";

        holder.binding.textViewFood.setText(MainActivity.getActualStringFood(order));
        holder.binding.textViewUUID.setText(uuid);
        holder.binding.textViewFeatures.setText(features);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }
}
