package com.foodie.service;

import com.foodie.dto.Dtos.DishRequest;
import com.foodie.entity.Dish;
import com.foodie.exception.NotFoundException;
import com.foodie.repository.DishRepository;
import com.foodie.repository.RestaurantRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DishService {
    private final DishRepository dishes;
    private final RestaurantRepository restaurants;

    public DishService(DishRepository dishes, RestaurantRepository restaurants) {
        this.dishes = dishes;
        this.restaurants = restaurants;
    }

    public List<Dish> search(Long restaurantId, String category, Boolean veg, BigDecimal maxPrice, String q) {
        String cat = (category == null || category.isBlank()) ? null : category;
        String query = (q == null || q.isBlank()) ? null : q.trim();
        return dishes.search(restaurantId, cat, veg, maxPrice, query);
    }

    public Dish findById(Long id) {
        return dishes.findById(id).orElseThrow(() -> new NotFoundException("Dish " + id + " not found"));
    }

    public Dish create(DishRequest req) {
        return dishes.save(apply(new Dish(), req));
    }

    public Dish update(Long id, DishRequest req) {
        return dishes.save(apply(findById(id), req));
    }

    public void delete(Long id) {
        dishes.delete(findById(id));
    }

    private Dish apply(Dish d, DishRequest req) {
        d.setRestaurant(restaurants.findById(req.restaurantId())
                .orElseThrow(() -> new NotFoundException("Restaurant " + req.restaurantId() + " not found")));
        d.setName(req.name());
        d.setDescription(req.description());
        d.setPrice(req.price());
        d.setCategory(req.category());
        d.setVeg(req.veg());
        d.setImageUrl(req.imageUrl());
        if (req.available() != null) d.setAvailable(req.available());
        return d;
    }
}
