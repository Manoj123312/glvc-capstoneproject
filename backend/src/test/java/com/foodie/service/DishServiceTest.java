package com.foodie.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.foodie.dto.Dtos.DishRequest;
import com.foodie.entity.Dish;
import com.foodie.entity.Restaurant;
import com.foodie.exception.NotFoundException;
import com.foodie.repository.DishRepository;
import com.foodie.repository.RestaurantRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DishServiceTest {
    @Mock DishRepository dishes;
    @Mock RestaurantRepository restaurants;
    @InjectMocks DishService service;

    @Test
    void create_linksRestaurantAndSaves() {
        Restaurant r = new Restaurant();
        r.setId(1L);
        when(restaurants.findById(1L)).thenReturn(Optional.of(r));
        when(dishes.save(any(Dish.class))).thenAnswer(i -> i.getArgument(0));

        Dish d = service.create(new DishRequest(1L, "Pizza", "tasty", new BigDecimal("250"), "Pizza", true, null, null));

        assertEquals("Pizza", d.getName());
        assertSame(r, d.getRestaurant());
        assertTrue(d.isAvailable());
    }

    @Test
    void create_unknownRestaurant_throws() {
        when(restaurants.findById(9L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> service.create(new DishRequest(9L, "X", null, BigDecimal.ONE, null, false, null, null)));
    }

    @Test
    void findById_missing_throws() {
        when(dishes.findById(3L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.findById(3L));
    }

    @Test
    void search_blankFiltersBecomeNull() {
        when(dishes.search(null, null, true, null, null)).thenReturn(List.of(new Dish()));
        assertEquals(1, service.search(null, " ", true, null, "").size());
    }

    @Test
    void delete_removesDish() {
        Dish d = new Dish();
        when(dishes.findById(4L)).thenReturn(Optional.of(d));
        service.delete(4L);
        verify(dishes).delete(d);
    }
}
