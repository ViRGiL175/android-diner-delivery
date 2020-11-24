package ru.commandos.server.controller;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.commandos.server.model.Feature;

import java.util.*;
import java.util.stream.Collectors;


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
