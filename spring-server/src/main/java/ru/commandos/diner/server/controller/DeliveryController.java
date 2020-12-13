package ru.commandos.diner.server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.commandos.diner.server.model.Order;
import ru.commandos.diner.server.model.OrderDelivery;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/delivery")
public class DeliveryController extends BaseComponent {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format("Hello %s!", name);
    }

    @GetMapping("/check")
    public Order check(@RequestParam UUID courierUuid) {
        return deliveryService.check(courierUuid);
    }

    @GetMapping("/status")
    public List<OrderDelivery> status(@RequestParam UUID courierUuid) {
        return deliveryService.status(courierUuid);
    }

    @GetMapping("/accept")
    public void accept(@RequestParam UUID courierUuid, @RequestParam UUID orderUuid) {
        deliveryService.accept(courierUuid, orderUuid);
    }

    @GetMapping("/handshake")
    public void handshake(@RequestParam UUID courierUuid, @RequestParam UUID orderUuid, @RequestParam UUID codeUuid) {
        deliveryService.handshake(courierUuid, orderUuid, codeUuid);
    }

//    @GetMapping("/pick_from_diner")
//    public void pick_from_diner(@RequestParam UUID courierUuid, @RequestParam UUID orderUuid) {
//        deliveryService.getWorkingOrders().add(new OrderDelivery(deliveryService.getPreparedOrders()
//                .get(orderUuid), courierUuid, DeliveryStatus.DELIVERY_TO_CLIENT));
//    }

//    @GetMapping("/courier_broken")
//    public void courier_broken(@RequestParam UUID courierUuid, @RequestParam UUID orderUuid) {
//        deliveryService.getWorkingOrders().add(new OrderDelivery(deliveryService.getPreparedOrders()
//                .get(orderUuid), courierUuid, DeliveryStatus.BROKEN));
//    }
}
