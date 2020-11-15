package ru.commandos.controller;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Component
@Scope("singleton")
public class OrderNamesProvider {

    public final List<String> drinks;
    public final List<String> meals;
    private final Random random = new Random();

    public OrderNamesProvider() {
        drinks = Collections.unmodifiableList(Arrays.asList(
                "Vodka",
                "Cola",
                "Tea",
                "Coffee",
                "Beer",
                "Juice"
        ));
        meals = Collections.unmodifiableList(Arrays.asList(
                "Hot Dog",
                "Cookie",
                "T-Bone",
                "Pelmeni",
                "Fried Eggs",
                "Pizza"
        ));
    }

    public String getRandom(boolean isDrink) {
        return isDrink
                ? drinks.get(random.nextInt(drinks.size()))
                : meals.get(random.nextInt(meals.size()));
    }
}
