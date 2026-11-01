package com.foodie.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.foodie.dto.Dtos.OrderDto;
import com.foodie.dto.Dtos.OrderItemRequest;
import com.foodie.dto.Dtos.OrderRequest;
import com.foodie.entity.*;
import com.foodie.exception.BadRequestException;
import com.foodie.exception.NotFoundException;
import com.foodie.repository.DishRepository;
import com.foodie.repository.OrderRepository;
import com.foodie.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock OrderRepository orders;
    @Mock DishRepository dishes;
    @Mock UserRepository users;
    @InjectMocks OrderService service;

    User user;
    Dish dish;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Demo");
        user.setEmail("user@foodie.com");
        Restaurant r = new Restaurant();
        r.setName("Spice Garden");
        dish = new Dish();
        dish.setId(10L);
        dish.setName("Biryani");
        dish.setPrice(new BigDecimal("100"));
        dish.setRestaurant(r);
    }

    @Test
    void placeOrder_calculatesTotalAndStatusPlaced() {
        when(users.findByEmail("user@foodie.com")).thenReturn(Optional.of(user));
        when(dishes.findById(10L)).thenReturn(Optional.of(dish));
        when(orders.save(any(OrderEntity.class))).thenAnswer(i -> i.getArgument(0));

        OrderDto dto = service.placeOrder("user@foodie.com",
                new OrderRequest("Hyderabad", List.of(new OrderItemRequest(10L, 3))));

        assertEquals(0, new BigDecimal("300").compareTo(dto.totalAmount()));
        assertEquals("PLACED", dto.status());
        assertEquals(1, dto.items().size());
    }

    @Test
    void placeOrder_unavailableDish_throws() {
        dish.setAvailable(false);
        when(users.findByEmail("user@foodie.com")).thenReturn(Optional.of(user));
        when(dishes.findById(10L)).thenReturn(Optional.of(dish));

        assertThrows(BadRequestException.class, () -> service.placeOrder("user@foodie.com",
                new OrderRequest("addr", List.of(new OrderItemRequest(10L, 1)))));
        verify(orders, never()).save(any());
    }

    @Test
    void placeOrder_unknownDish_throwsNotFound() {
        when(users.findByEmail("user@foodie.com")).thenReturn(Optional.of(user));
        when(dishes.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.placeOrder("user@foodie.com",
                new OrderRequest("addr", List.of(new OrderItemRequest(99L, 1)))));
    }

    @Test
    void cancel_placedOrder_setsCancelled() {
        OrderEntity o = order(OrderEntity.Status.PLACED);
        when(orders.findById(5L)).thenReturn(Optional.of(o));
        when(orders.save(o)).thenReturn(o);

        assertEquals("CANCELLED", service.cancel("user@foodie.com", 5L).status());
    }

    @Test
    void cancel_deliveredOrder_throws() {
        when(orders.findById(5L)).thenReturn(Optional.of(order(OrderEntity.Status.DELIVERED)));
        assertThrows(BadRequestException.class, () -> service.cancel("user@foodie.com", 5L));
    }

    @Test
    void cancel_otherUsersOrder_throwsNotFound() {
        when(orders.findById(5L)).thenReturn(Optional.of(order(OrderEntity.Status.PLACED)));
        assertThrows(NotFoundException.class, () -> service.cancel("other@foodie.com", 5L));
    }

    @Test
    void updateStatus_changesStatus() {
        OrderEntity o = order(OrderEntity.Status.PLACED);
        when(orders.findById(5L)).thenReturn(Optional.of(o));
        when(orders.save(o)).thenReturn(o);

        assertEquals("DELIVERED", service.updateStatus(5L, OrderEntity.Status.DELIVERED).status());
    }

    private OrderEntity order(OrderEntity.Status status) {
        OrderEntity o = new OrderEntity();
        o.setId(5L);
        o.setUser(user);
        o.setStatus(status);
        return o;
    }
}
