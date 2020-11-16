package ru.commandos.diner.delivery;

import ru.commandos.diner.delivery.model.Order;

public class FoodGetter {

    public static String getActualStringFood(Order currentOrder) {
        StringBuilder food = new StringBuilder(currentOrder.items.get(0).name);
        for (int i = 1; i < currentOrder.items.size(); i++) {
            food.append(", ");
            String c = currentOrder.items.get(i).name;
            c = c.toLowerCase();
            food.append(c);
        }
        return String.valueOf(food);
    }
}
