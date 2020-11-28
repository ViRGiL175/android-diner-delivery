package ru.commandos.diner.server.controller;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class CouriersProvider extends BaseComponent {

    public final List<UUID> courierUuids = new ArrayList<>();

    public CouriersProvider() {
        courierUuids.add(UUID.fromString("df307a18-1b66-432a-8011-39b68397d000"));
    }
}
