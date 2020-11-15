package ru.commandos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import ru.commandos.model.Item;
import ru.commandos.model.Order;
import ru.commandos.model.OrderDelivery;

@Service
public class DeliveryService {

    private final OrderNamesProvider orderNamesProvider;
    private final Map<UUID, Order> preparedOrders = new HashMap<>();
    private final Map<UUID, OrderDelivery> workingOrders = new HashMap<>();

    public DeliveryService(@Autowired OrderNamesProvider orderNamesProvider) {
        this.orderNamesProvider = orderNamesProvider;
    }

    public Map<UUID, Order> getPreparedOrders() {
        return preparedOrders;
    }

    public Map<UUID, OrderDelivery> getWorkingOrders() {
        return workingOrders;
    }

    public void generateOrder() {
        Random random = new Random();
        int ordersCount = random.nextInt(10) + 5;
        for (int i = 0; i < ordersCount; i++) {
            Order order = new Order();
            int itemsCount = random.nextInt(5);
            for (int j = 0; j < itemsCount; j++) {
                boolean isDrink = random.nextBoolean();
                Item item = new Item(orderNamesProvider.getRandom(isDrink),
                        random.nextFloat() * 1000 + 200);
                order.items.add(item);
            }
            preparedOrders.put(UUID.randomUUID(), order);
        }
    }
}
