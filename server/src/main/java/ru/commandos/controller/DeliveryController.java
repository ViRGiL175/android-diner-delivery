package ru.commandos.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

import ru.commandos.model.DeliveryStatus;
import ru.commandos.model.Order;
import ru.commandos.model.OrderDelivery;

@RestController
@RequestMapping("/delivery")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(@Autowired DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
        deliveryService.generateOrder();
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format("Hello %s!", name);
    }

    @GetMapping("/orders")
    public Map<UUID, Order> orders(@RequestParam UUID courierUuid) {
        return deliveryService.getPreparedOrders();
    }

    @GetMapping("/accept")
    public void accept(@RequestParam UUID courierUuid, @RequestParam UUID orderUuid) {
        deliveryService.getWorkingOrders().put(orderUuid, new OrderDelivery(deliveryService.getPreparedOrders().get(orderUuid),
                courierUuid, DeliveryStatus.PICKING_ORDER));
        deliveryService.getPreparedOrders().remove(orderUuid);
    }

    @GetMapping("/pick_from_diner")
    public void pick_from_diner(@RequestParam UUID courierUuid, @RequestParam UUID orderUuid) {
        deliveryService.getWorkingOrders().put(orderUuid, new OrderDelivery(deliveryService.getPreparedOrders().get(orderUuid),
                courierUuid, DeliveryStatus.DELIVERY_TO_CLIENT));
    }

    @GetMapping("/delivery_handshake")
    public void delivery_handshake(@RequestParam UUID courierUuid, @RequestParam UUID orderUuid) {
        deliveryService.getWorkingOrders().put(orderUuid, new OrderDelivery(deliveryService.getPreparedOrders().get(orderUuid),
                courierUuid, DeliveryStatus.COMPLETED));
    }

    @GetMapping("/courier_broken")
    public void courier_broken(@RequestParam UUID courierUuid, @RequestParam UUID orderUuid) {
        deliveryService.getWorkingOrders().put(orderUuid, new OrderDelivery(deliveryService.getPreparedOrders().get(orderUuid),
                courierUuid, DeliveryStatus.BROKEN));
    }
}
