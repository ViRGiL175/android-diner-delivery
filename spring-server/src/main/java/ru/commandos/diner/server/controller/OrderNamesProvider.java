package ru.commandos.diner.server.controller;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.commandos.diner.server.model.Feature;

import java.util.*;
import java.util.stream.Collectors;


@Component
@Scope("singleton")
public class OrderNamesProvider extends BaseComponent {

    public final Map<String, Feature[]> orderItems;

    public OrderNamesProvider() {
        orderItems = Map.ofEntries(
                Map.entry("Vodka", new Feature[]{Feature.SHOULD_BE_COLD, Feature.LIQUID}),
                Map.entry("Cola", new Feature[]{Feature.LIQUID}),
                Map.entry("Tea", new Feature[]{Feature.SHOULD_BE_HOT, Feature.LIQUID}),
                Map.entry("Coffee", new Feature[]{Feature.SHOULD_BE_HOT, Feature.LIQUID}),
                Map.entry("Beer", new Feature[]{Feature.SHOULD_BE_COLD, Feature.LIQUID}),
                Map.entry("Juice", new Feature[]{Feature.LIQUID}),
                Map.entry("Hot Dog", new Feature[]{Feature.SHOULD_BE_HOT}),
                Map.entry("Cookie", new Feature[]{}),
                Map.entry("T-Bone", new Feature[]{Feature.SHOULD_BE_HOT}),
                Map.entry("Pelmeni", new Feature[]{Feature.SHOULD_BE_HOT}),
                Map.entry("Fried Eggs", new Feature[]{Feature.SHOULD_BE_HOT}),
                Map.entry("Pizza", new Feature[]{Feature.SHOULD_BE_HOT}));
    }

    public Map.Entry<String, Feature[]> getRandom(boolean isDrink) {
        List<Map.Entry<String, Feature[]>> collect = orderItems.entrySet().stream()
                .filter(stringEntry -> isDrink == Arrays.asList(stringEntry.getValue()).contains(Feature.LIQUID))
                .collect(Collectors.toList());
        Collections.shuffle(collect);
        return collect.stream().findFirst().orElse(
                new AbstractMap.SimpleEntry<>("Null Item", new Feature[]{}));
    }
}
