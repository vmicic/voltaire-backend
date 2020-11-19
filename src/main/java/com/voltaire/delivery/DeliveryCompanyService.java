package com.voltaire.delivery;

import com.voltaire.delivery.model.CreateDeliveryCompanyRequest;
import com.voltaire.delivery.model.DeliveryCompany;
import com.voltaire.delivery.repository.DeliveryCompanyRepository;
import com.voltaire.exception.customexceptions.BadRequestException;
import com.voltaire.order.model.Order;
import com.voltaire.order.model.OrderForDeliveryRequest;
import com.voltaire.order.model.OrderStatus;
import com.voltaire.order.repository.OrderRepository;
import com.voltaire.restaurant.repository.RestaurantRepository;
import com.voltaire.shared.GeocodeService;
import com.voltaire.shared.Geolocation;
import com.voltaire.shared.IdResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryCompanyService {

    @Value("${voltaire.orders.delivery-timeout}")
    private Integer confirmedOrderDeliveryTimeoutMinutes;

    private final OrderRepository orderRepository;

    private final RestaurantRepository restaurantRepository;

    private final DeliveryCompanyRepository deliveryCompanyRepository;

    private final GeocodeService geocodeService;

    private final Clock clock;

    public void createDeliveryCompany(CreateDeliveryCompanyRequest createDeliveryCompanyRequest) {
        var deliveryCompany = DeliveryCompany.builder()
                .name(createDeliveryCompanyRequest.getName())
                .build();

        deliveryCompanyRepository.save(deliveryCompany);
    }

    public List<OrderForDeliveryRequest> getOrdersForDelivery(String address) {
        if (address == null) {
            return getOrdersForDelivery();
        } else {
            return getSortedByPickupDistanceOrdersForDelivery(address);
        }
    }

    public List<OrderForDeliveryRequest> getOrdersForDelivery() {
        var orders = findAllOrdersForDelivery();
        var ordersForDelivery = new ArrayList<OrderForDeliveryRequest>();

        orders.forEach(
                order -> {
                    var restaurant = restaurantRepository.findById(order.getRestaurantId());
                    var orderForDelivery = OrderForDeliveryRequest.builder()
                            .orderId(order.getId())
                            .restaurantName(restaurant.getName())
                            .restaurantAddress(restaurant.getAddress())
                            .deliveryAddress(order.getDeliveryAddress())
                            .minutesForPreparation(order.getMinutesForPreparation())
                            .build();

                    ordersForDelivery.add(orderForDelivery);
                }
        );
        return ordersForDelivery;
    }

    private List<Order> findAllOrdersForDelivery() {
        var timeCutoff = new Date(System.currentTimeMillis() - 60 * confirmedOrderDeliveryTimeoutMinutes * 1000);
        return orderRepository.findAllOrdersForDelivery(timeCutoff);
    }

    public Order findOrderById(String id) {
        return orderRepository.findById(id);
    }

    public IdResponse takeOrderToDeliver(String id) {
        var order = findOrderById(id);

        if (order.notWaitingDeliveryService()) {
            throw new BadRequestException("Requested order is not waiting delivery service response.");
        }

        order.setOrderStatus(OrderStatus.PREPARING);
        return new IdResponse(id);
    }

    public IdResponse startDelivery(String id) {
        var order = findOrderById(id);

        if (order.notWaitingDeliveryStart()) {
            throw new BadRequestException("Requested order is not waiting delivery start.");
        }

        order.setOrderStatus(OrderStatus.DELIVERING);
        return new IdResponse(id);
    }

    public IdResponse orderDelivered(String id) {
        var order = findOrderById(id);

        if (order.notWaitingDeliveryConfirmation()) {
            throw new BadRequestException("Requested order is not waiting delivery confirmation.");
        }

        order.setOrderStatus(OrderStatus.DELIVERED);
        return new IdResponse(id);
    }

    public List<OrderForDeliveryRequest> getSortedByPickupDistanceOrdersForDelivery(String address) {
        var deliverymanPoint = geocodeService.getGeolocationForAddressString(address);
        var orders = findAllOrdersForDelivery();

        var ordersForDelivery = mapOrdersToOrdersForDelivery(orders, deliverymanPoint);
        return ordersForDelivery.stream().sorted(
                Comparator.comparing(OrderForDeliveryRequest::getRestaurantDistanceInMeters)).collect(Collectors.toList());
    }

    private List<OrderForDeliveryRequest> mapOrdersToOrdersForDelivery(List<Order> orders, Geolocation deliverymanGeolocation) {
        var ordersForDelivery = new ArrayList<OrderForDeliveryRequest>();
        orders.forEach(order -> {
            var restaurant = restaurantRepository.findById(order.getRestaurantId());
            var orderForDelivery = OrderForDeliveryRequest.builder()
                    .orderId(order.getId())
                    .restaurantName(restaurant.getName())
                    .restaurantAddress(restaurant.getAddress())
                    .deliveryAddress(order.getDeliveryAddress())
                    .minutesForPreparation(order.getMinutesForPreparation())
                    .restaurantDistanceInMeters((int) Math.round(geocodeService.distance(restaurant.getGeolocation(), deliverymanGeolocation)))
                    .build();

            ordersForDelivery.add(orderForDelivery);
        });

        return ordersForDelivery;
    }
}
