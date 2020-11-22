package ru.commandos.diner.delivery.model;

import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import retrofit2.internal.EverythingIsNonNull;

@EverythingIsNonNull
public class Order extends BaseApiModel {

    @Nullable
    protected final String uuid;
    @Nullable
    protected final List<Item> items;

    public Order(@Nullable String uuid, @Nullable List<Item> items) {
        this.uuid = uuid;
        this.items = items;
    }

    public String getUuid() {
        return Optional.ofNullable(uuid).orElse("00000000-0000-0000-0000-000000000000");
    }

    public List<Item> getItems() {
        return Optional.ofNullable(items).orElse(Collections.singletonList(
                new Item(null, 0, null)));
    }

    public String getReadableMass() {
        return getItems().stream()
                .mapToLong(value -> (long) value.getMass())
                .sum() + " кг";
    }

    public String getReadableContent() {
        return getItems().stream()
                .map(Item::getName)
                .collect(Collectors.joining(", "));
    }

    public String getReadableFeatures() {
        Map<Feature, String> readableFeatures = new HashMap<Feature, String>() {{
            put(Feature.LIQUID, "есть жидкости");
            put(Feature.SHOULD_BE_COLD, "должно быть холодным");
            put(Feature.SHOULD_BE_HOT, "должно быть горячим");
        }};
        String result = getItems().stream()
                .flatMap(item -> Arrays.stream(item.getFeatures()))
                .distinct()
                .map(readableFeatures::get)
                .collect(Collectors.joining(", "));
        result = !result.isEmpty()
                ? result.substring(0, 1).toUpperCase() + result.substring(1) + "!"
                : "Без особенностей";
        return result;
    }

    @Override
    public String toString() {
        return "Order{" +
                "uuid=" + getUuid() +
                ", items=" + getItems() +
                '}';
    }
}
