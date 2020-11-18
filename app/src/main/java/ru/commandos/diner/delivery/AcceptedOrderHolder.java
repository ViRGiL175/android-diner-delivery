package ru.commandos.diner.delivery;

import androidx.recyclerview.widget.RecyclerView;

import retrofit2.internal.EverythingIsNonNull;
import ru.commandos.diner.delivery.databinding.AcceptedOrderBinding;

@EverythingIsNonNull
public class AcceptedOrderHolder extends RecyclerView.ViewHolder {

    public AcceptedOrderBinding binding;

    public AcceptedOrderHolder(AcceptedOrderBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
