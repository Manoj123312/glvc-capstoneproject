package com.foodie.service;

import com.foodie.dto.Dtos.OrderDto;
import com.foodie.dto.Dtos.OrderItemRequest;
import com.foodie.dto.Dtos.OrderRequest;
import com.foodie.entity.Dish;
import com.foodie.entity.OrderEntity;
import com.foodie.entity.OrderEntity.Status;
import com.foodie.entity.OrderItem;
import com.foodie.entity.User;
import com.foodie.exception.BadRequestException;
import com.foodie.exception.NotFoundException;
import com.foodie.repository.DishRepository;
import com.foodie.repository.OrderRepository;
import com.foodie.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private final OrderRepository orders;
    private final DishRepository dishes;
    private final UserRepository users;

    public OrderService(OrderRepository orders, DishRepository dishes, UserRepository users) {
        this.orders = orders;
        this.dishes = dishes;
        this.users = users;
    }

    @Transactional
    public OrderDto placeOrder(String email, OrderRequest req) {
        User user = users.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
        OrderEntity order = new OrderEntity();
        order.setUser(user);
        order.setDeliveryAddress(req.deliveryAddress());
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemRequest ir : req.items()) {
            Dish dish = dishes.findById(ir.dishId())
                    .orElseThrow(() -> new NotFoundException("Dish " + ir.dishId() + " not found"));
            if (!dish.isAvailable()) throw new BadRequestException(dish.getName() + " is currently unavailable");
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setDishId(dish.getId());
            item.setDishName(dish.getName());
            item.setRestaurantName(dish.getRestaurant().getName());
            item.setUnitPrice(dish.getPrice());
            item.setQuantity(ir.quantity());
            order.getItems().add(item);
            total = total.add(dish.getPrice().multiply(BigDecimal.valueOf(ir.quantity())));
        }
        order.setTotalAmount(total);
        return OrderDto.from(orders.save(order));
    }

    public List<OrderDto> myOrders(String email) {
        User user = users.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
        return orders.findByUserIdOrderByCreatedAtDesc(user.getId()).stream().map(OrderDto::from).toList();
    }

    public List<OrderDto> allOrders() {
        return orders.findAllByOrderByCreatedAtDesc().stream().map(OrderDto::from).toList();
    }

    @Transactional
    public OrderDto cancel(String email, Long orderId) {
        OrderEntity o = get(orderId);
        if (!o.getUser().getEmail().equalsIgnoreCase(email)) throw new NotFoundException("Order " + orderId + " not found");
        if (o.getStatus() != Status.PLACED && o.getStatus() != Status.CONFIRMED) {
            throw new BadRequestException("Order can no longer be cancelled (status: " + o.getStatus() + ")");
        }
        o.setStatus(Status.CANCELLED);
        return OrderDto.from(orders.save(o));
    }

    @Transactional
    public OrderDto updateStatus(Long orderId, Status status) {
        OrderEntity o = get(orderId);
        o.setStatus(status);
        return OrderDto.from(orders.save(o));
    }

    private OrderEntity get(Long id) {
        return orders.findById(id).orElseThrow(() -> new NotFoundException("Order " + id + " not found"));
    }
}
