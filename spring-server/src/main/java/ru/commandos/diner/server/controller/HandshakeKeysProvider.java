package ru.commandos.diner.server.controller;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class HandshakeKeysProvider {

    public UUID get() {
        return UUID.fromString("65f3621b-1b5d-4951-8729-fb34f5f1cc8e");
    }
}
