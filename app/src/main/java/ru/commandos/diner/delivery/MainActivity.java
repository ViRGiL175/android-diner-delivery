package ru.commandos.diner.delivery;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ru.commandos.diner.delivery.controller.OrderAcceptingController;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OrderAcceptingController controller = new OrderAcceptingController("df307a18-1b66-432a-8011-39b68397d000");
        controller.check();
    }
}