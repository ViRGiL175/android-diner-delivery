package ru.commandos.diner.server.controller;

import org.springframework.stereotype.Component;
import ru.commandos.diner.server.model.Location;

@Component
public class LocationProvider extends BaseComponent {

    private final Location izhevsk = new Location(56.85306, 53.21222);

    public Location getDinerLocation() {
        return izhevsk;
    }

    public Location getDestination() {
        return new Location(izhevsk.latitude + (Math.random() - 1) / 5, izhevsk.longitude + (Math.random() - 1) / 5);
    }
}
