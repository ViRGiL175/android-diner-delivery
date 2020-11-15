package ru.commandos.controller;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ru.commandos.model.Feature;

@Component
@Scope("singleton")
public class OrderNamesProvider {

    public final Map<String, Feature[]> orderItems;

    public OrderNamesProvider() {
        orderItems = Collections.unmodifiableMap(new HashMap<String, Feature[]>() {{
            put("Vodka", new Feature[]{Feature.SHOULD_BE_COLD, Feature.LIQUID});
            put("Cola", new Feature[]{Feature.LIQUID});
            put("Tea", new Feature[]{Feature.SHOULD_BE_HOT, Feature.LIQUID});
            put("Coffee", new Feature[]{Feature.SHOULD_BE_HOT, Feature.LIQUID});
            put("Beer", new Feature[]{Feature.SHOULD_BE_COLD, Feature.LIQUID});
            put("Juice", new Feature[]{Feature.LIQUID});
            put("Hot Dog", new Feature[]{Feature.SHOULD_BE_HOT});
            put("Cookie", new Feature[]{});
            put("T-Bone", new Feature[]{Feature.SHOULD_BE_HOT});
            put("Pelmeni", new Feature[]{Feature.SHOULD_BE_HOT});
            put("Fried Eggs", new Feature[]{Feature.SHOULD_BE_HOT});
            put("Pizza", new Feature[]{Feature.SHOULD_BE_HOT});
        }});
    }

    public Map.Entry<String, Feature[]> getRandom(boolean isDrink) {
        List<Map.Entry<String, Feature[]>> collect = orderItems.entrySet().stream()
                .filter(stringEntry -> isDrink == Arrays.asList(stringEntry.getValue())
                        .contains(Feature.LIQUID))
                .collect(Collectors.toList());
        Collections.shuffle(collect);
        return collect.stream().findFirst().orElse(
                new AbstractMap.SimpleEntry<>("Null Item", new Feature[]{}));
    }
}
