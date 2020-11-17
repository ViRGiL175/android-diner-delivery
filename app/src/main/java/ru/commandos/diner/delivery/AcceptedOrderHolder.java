package ru.commandos.diner.delivery;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.commandos.diner.delivery.databinding.AcceptedOrderBinding;

public class AcceptedOrderHolder extends RecyclerView.ViewHolder {
    public AcceptedOrderHolder(@NonNull View itemView) {
        super(itemView);
    }

    public AcceptedOrderBinding binding;

    public AcceptedOrderHolder(AcceptedOrderBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
