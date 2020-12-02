package ru.commandos.diner.delivery.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import retrofit2.internal.EverythingIsNonNull;
import ru.commandos.diner.delivery.controller.DpConverter;
import timber.log.Timber;

@EverythingIsNonNull
public class OrdersBackdropBehavior extends BottomSheetBehavior<View> {

    private float lastY;

    public OrdersBackdropBehavior() {
    }

    public OrdersBackdropBehavior(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onNestedFling(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child,
                                 @NonNull View target, float velocityX, float velocityY, boolean consumed) {
        Timber.e("X: " + velocityX + ", Y: " + velocityY);
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull CoordinatorLayout parent, @NonNull View child,
                                         @NonNull MotionEvent event) {
        CardView cardView = (CardView) child;
        Rect cardViewArea = new Rect();
        cardView.getHitRect(cardViewArea);
        if (cardViewArea.contains((int) event.getX(), (int) event.getY())) {
            cardView.setRadius(lastY > event.getY()
                    ? DpConverter.convertDpToPixel(0)
                    : DpConverter.convertDpToPixel(16));
            lastY = event.getY();
        }
        return super.onInterceptTouchEvent(parent, child, event);
    }
}
