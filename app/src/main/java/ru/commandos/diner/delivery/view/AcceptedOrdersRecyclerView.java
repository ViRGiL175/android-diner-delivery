package ru.commandos.diner.delivery.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Optional;

import ru.commandos.diner.delivery.model.Order;

public class AcceptedOrdersRecyclerView extends RecyclerView {

    public AcceptedOrdersRecyclerView(@NonNull Context context) {
        super(context);
    }

    public AcceptedOrdersRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AcceptedOrdersRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setHasFixedSize(true);
        setLayoutManager(new LinearLayoutManager(getContext()));
        setAdapter(getAdapter());
    }

    public void setOrders(List<Order> orders) {
        getAdapter().setOrders(orders);
    }

    @Override
    @NonNull
    public AcceptedOrdersAdapter getAdapter() {
        return Optional.ofNullable(((AcceptedOrdersAdapter) super.getAdapter()))
                .orElseGet(() -> {
                    AcceptedOrdersAdapter adapter = new AcceptedOrdersAdapter();
                    setAdapter(adapter);
                    return adapter;
                });
    }
}
