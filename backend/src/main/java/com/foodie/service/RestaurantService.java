package com.foodie.service;

import com.foodie.entity.Restaurant;
import com.foodie.entity.Dish;
import com.foodie.exception.NotFoundException;
import com.foodie.repository.DishRepository;
import com.foodie.repository.RestaurantRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RestaurantService {
    private final RestaurantRepository restaurants;
    private final DishRepository dishes;

    public RestaurantService(RestaurantRepository restaurants, DishRepository dishes) {
        this.restaurants = restaurants;
        this.dishes = dishes;
    }

    public List<Restaurant> findAll() { return restaurants.findAll(); }

    public Restaurant findById(Long id) {
        return restaurants.findById(id).orElseThrow(() -> new NotFoundException("Restaurant " + id + " not found"));
    }

    public Restaurant create(Restaurant r) {
        r.setId(null);
        return restaurants.save(r);
    }

    public Restaurant update(Long id, Restaurant r) {
        Restaurant existing = findById(id);
        existing.setName(r.getName());
        existing.setCuisine(r.getCuisine());
        existing.setCity(r.getCity());
        existing.setAddress(r.getAddress());
        existing.setImageUrl(r.getImageUrl());
        existing.setRating(r.getRating());
        return restaurants.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        Restaurant r = findById(id);
        List<Dish> owned = dishes.search(id, null, null, null, null);
        dishes.deleteAll(owned);
        restaurants.delete(r);
    }
}
