package com.voltaire.delivery;

import com.voltaire.delivery.model.CreateDeliveryCompanyRequest;
import com.voltaire.delivery.model.DeliveryCompany;
import com.voltaire.delivery.repository.DeliveryCompanyRepository;
import com.voltaire.exception.customexceptions.BadRequestException;
import com.voltaire.exception.customexceptions.EntityNotFoundException;
import com.voltaire.order.model.Order;
import com.voltaire.order.model.OrderForDelivery;
import com.voltaire.order.model.OrderStatus;
import com.voltaire.order.repository.OrderRepository;
import com.voltaire.security.SecretManagerService;
import com.voltaire.shared.GeocodeService;
import com.voltaire.shared.Geolocation;
import com.voltaire.shared.IdResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryCompanyService {

    @Value("${voltaire.orders.delivery-timeout}")
    private Integer confirmedOrderDeliveryTimeout;

    private final OrderRepository orderRepository;

    private final DeliveryCompanyRepository deliveryCompanyRepository;

    private final GeocodeService geocodeService;

    private final SecretManagerService secretManagerService;

    private final Clock clock;

    public DeliveryCompany createDeliveryCompany(CreateDeliveryCompanyRequest createDeliveryCompanyRequest) {
        var deliveryCompany = DeliveryCompany.builder()
                .name(createDeliveryCompanyRequest.getName())
                .build();

        var secretId = deliveryCompany.getName().replace(" ", "-");

        secretManagerService.createSecret(secretId, UUID.randomUUID().toString());

        return deliveryCompanyRepository.save(deliveryCompany);
    }

    public List<OrderForDelivery> getOrdersForDelivery(String address) {
        if (address == null) {
            return getOrdersForDelivery();
        } else {
            return getSortedByPickupDistanceOrdersForDelivery(address);
        }
    }

    public List<OrderForDelivery> getOrdersForDelivery() {
        var orders = findAllOrdersForDelivery();
        var ordersForDelivery = new ArrayList<OrderForDelivery>();

        orders.forEach(
                order -> {
                    var orderForDelivery = OrderForDelivery.builder()
                            .orderId(order.getId())
                            .restaurantName(order.getRestaurant().getName())
                            .restaurantAddress(order.getRestaurant().getAddress())
                            .deliveryAddress(order.getDeliveryAddress())
                            .minutesForPreparation(order.getMinutesForPreparation())
                            .build();

                    ordersForDelivery.add(orderForDelivery);
                }
        );
        return ordersForDelivery;
    }

    private List<Order> findAllOrdersForDelivery() {
        var timeCutoff = LocalDateTime.now(clock).minusMinutes(confirmedOrderDeliveryTimeout);
        return orderRepository.findAllByOrderTimeAfterAndOrderStatusEquals(timeCutoff, OrderStatus.CONFIRMED);
    }

    public Order findOrderById(UUID id) {
        return orderRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("id", id.toString()));
    }

    public IdResponse takeOrderToDeliver(UUID id) {
        var order = findOrderById(id);

        if (order.notWaitingDeliveryService()) {
            throw new BadRequestException("Requested order is not waiting delivery service response.");
        }

        order.setOrderStatus(OrderStatus.PREPARING);
        return new IdResponse(orderRepository.save(order).getId());
    }

    public IdResponse startDelivery(UUID id) {
        var order = findOrderById(id);

        if (order.notWaitingDeliveryStart()) {
            throw new BadRequestException("Requested order is not waiting delivery start.");
        }

        order.setOrderStatus(OrderStatus.DELIVERING);
        return new IdResponse(orderRepository.save(order).getId());
    }

    public IdResponse orderDelivered(UUID id) {
        var order = findOrderById(id);

        if (order.notWaitingDeliveryConfirmation()) {
            throw new BadRequestException("Requested order is not waiting delivery confirmation.");
        }

        order.setOrderStatus(OrderStatus.DELIVERED);
        return new IdResponse(orderRepository.save(order).getId());
    }

    public List<OrderForDelivery> getSortedByPickupDistanceOrdersForDelivery(String address) {
        var deliverymanPoint = geocodeService.getGeolocationForAddressString(address);
        var orders = findAllOrdersForDelivery();

        var ordersForDelivery = mapOrdersToOrdersForDelivery(orders, deliverymanPoint);
        return ordersForDelivery.stream().sorted(
                Comparator.comparing(OrderForDelivery::getRestaurantDistanceInMeters)).collect(Collectors.toList());
    }

    private List<OrderForDelivery> mapOrdersToOrdersForDelivery(List<Order> orders, Geolocation deliverymanGeolocation) {
        var ordersForDelivery = new ArrayList<OrderForDelivery>();
        orders.forEach(order -> {
            var orderForDelivery = OrderForDelivery.builder()
                    .orderId(order.getId())
                    .restaurantName(order.getRestaurant().getName())
                    .restaurantAddress(order.getRestaurant().getAddress())
                    .deliveryAddress(order.getDeliveryAddress())
                    .minutesForPreparation(order.getMinutesForPreparation())
                    .restaurantDistanceInMeters((int) Math.round(geocodeService.distance(order.getRestaurant().getGeolocation(), deliverymanGeolocation)))
                    .build();

            ordersForDelivery.add(orderForDelivery);
        });

        return ordersForDelivery;
    }
}
