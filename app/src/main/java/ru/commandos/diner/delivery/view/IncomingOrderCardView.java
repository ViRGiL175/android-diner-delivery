package ru.commandos.diner.delivery.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import retrofit2.internal.EverythingIsNonNull;
import ru.commandos.diner.delivery.databinding.IncomingOrderBinding;
import ru.commandos.diner.delivery.model.Order;

@EverythingIsNonNull
public class IncomingOrderCardView extends CardView {

    @Nullable
    private IncomingOrderBinding binding;

    public IncomingOrderCardView(@NonNull Context context) {
        super(context);
    }

    public IncomingOrderCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public IncomingOrderCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Nullable
    public IncomingOrderBinding getBinding() {
        return binding;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        binding = IncomingOrderBinding.inflate(LayoutInflater.from(getContext()), this, true);
    }

    private int goneVisibility(boolean visibility) {
        return visibility ? View.VISIBLE : View.GONE;
    }

    public void showIncomingOrder(@Nullable Order order) {
        setIncomingOrderViewsVisibility(order != null);
        if (order != null) {
            post(() -> {
                binding.incomingOrderWarning.setText("Новый заказ!");
                binding.incomingUuid.setText(order.getUuid());
                binding.incomingContent.setText(order.getReadableContent());
                binding.incomingMass.setText(order.getReadableMass());
                binding.incomingFeatures.setText(order.getReadableFeatures());
            });
        } else {
            post(() -> binding.incomingOrderWarning.setText("Ожидайте новый заказ..."));
        }
    }

    public void setIncomingOrderViewsVisibility(boolean visibility) {
        post(() -> {
            binding.incomingAcceptButton.setVisibility(goneVisibility(visibility));
            binding.incomingDenyButton.setVisibility(goneVisibility(visibility));
            binding.incomingUuid.setVisibility(goneVisibility(visibility));
            binding.incomingUuidLabel.setVisibility(goneVisibility(visibility));
            binding.incomingContent.setVisibility(goneVisibility(visibility));
            binding.incomingContentLabel.setVisibility(goneVisibility(visibility));
            binding.incomingFeatures.setVisibility(goneVisibility(visibility));
            binding.incomingMass.setVisibility(goneVisibility(visibility));
            binding.incomingMassLabel.setVisibility(goneVisibility(visibility));
        });
    }
}
