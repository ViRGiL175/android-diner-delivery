package ru.commandos.diner.delivery.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import java.util.Optional;

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

    public IncomingOrderBinding getBinding() {
        return Optional.ofNullable(binding).orElseThrow(InflateException::new);
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
                getBinding().incomingOrderWarning.setText("Новый заказ!");
                getBinding().incomingUuid.setText(order.getUuid());
                getBinding().incomingContent.setText(order.getReadableContent());
                getBinding().incomingMass.setText(order.getReadableMass());
                getBinding().incomingFeatures.setText(order.getReadableFeatures());
            });
        } else {
            post(() -> getBinding().incomingOrderWarning.setText("Ожидайте новый заказ..."));
        }
    }

    public void setIncomingOrderViewsVisibility(boolean visibility) {
        post(() -> {
            getBinding().incomingAcceptButton.setVisibility(goneVisibility(visibility));
            getBinding().incomingDenyButton.setVisibility(goneVisibility(visibility));
            getBinding().incomingUuid.setVisibility(goneVisibility(visibility));
            getBinding().incomingUuidLabel.setVisibility(goneVisibility(visibility));
            getBinding().incomingContent.setVisibility(goneVisibility(visibility));
            getBinding().incomingContentLabel.setVisibility(goneVisibility(visibility));
            getBinding().incomingFeatures.setVisibility(goneVisibility(visibility));
            getBinding().incomingMass.setVisibility(goneVisibility(visibility));
            getBinding().incomingMassLabel.setVisibility(goneVisibility(visibility));
        });
    }

    public void onOfflineModeChecked(Boolean checked) {
        if (checked) {
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
            showIncomingOrder(null);
        }
    }
}
