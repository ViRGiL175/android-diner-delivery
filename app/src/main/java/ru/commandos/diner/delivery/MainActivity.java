package ru.commandos.diner.delivery;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import ru.commandos.diner.delivery.controller.OrderAcceptingController;

public class MainActivity extends AppCompatActivity {

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OrderAcceptingController controller = new OrderAcceptingController("df307a18-1b66-432a-8011-39b68397d000", compositeDisposable);
        controller.check();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}