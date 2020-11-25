package ru.commandos.diner.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.commandos.diner.server.model.DeliveryStatus;
import ru.commandos.diner.server.model.Order;
import ru.commandos.diner.server.model.OrderDelivery;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/delivery")
public class DeliveryController extends BaseComponent {

    private final DeliveryService deliveryService;

    public DeliveryController(@Autowired DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format("Hello %s!", name);
    }

    @GetMapping("/check")
    public Map<UUID, Order> check(@RequestParam UUID courierUuid) {
        return deliveryService.getPreparedOrders().entrySet().stream()
                .filter(uuidOrderEntry -> uuidOrderEntry.getKey().equals(courierUuid))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @GetMapping("/status")
    public List<OrderDelivery> status(@RequestParam UUID courierUuid) {
        return deliveryService.getWorkingOrders().stream()
                .filter(orderDelivery -> orderDelivery.courierUuid.equals(courierUuid))
                .collect(Collectors.toList());
    }

    @GetMapping("/accept")
    public void accept(@RequestParam UUID courierUuid, @RequestParam UUID orderUuid) {
        deliveryService.getWorkingOrders().add(new OrderDelivery(deliveryService.getPreparedOrders()
                .get(orderUuid), courierUuid, DeliveryStatus.PICKING_ORDER));
        deliveryService.getPreparedOrders().remove(orderUuid);
    }

    @GetMapping("/pick_from_diner")
    public void pick_from_diner(@RequestParam UUID courierUuid, @RequestParam UUID orderUuid) {
        deliveryService.getWorkingOrders().add(new OrderDelivery(deliveryService.getPreparedOrders()
                .get(orderUuid), courierUuid, DeliveryStatus.DELIVERY_TO_CLIENT));
    }

    @GetMapping("/delivery_handshake")
    public void delivery_handshake(@RequestParam UUID courierUuid, @RequestParam UUID orderUuid) {
        deliveryService.getWorkingOrders().add(new OrderDelivery(deliveryService.getPreparedOrders()
                .get(orderUuid), courierUuid, DeliveryStatus.COMPLETED));
    }

    @GetMapping("/courier_broken")
    public void courier_broken(@RequestParam UUID courierUuid, @RequestParam UUID orderUuid) {
        deliveryService.getWorkingOrders().add(new OrderDelivery(deliveryService.getPreparedOrders()
                .get(orderUuid), courierUuid, DeliveryStatus.BROKEN));
    }
}
