package com.foodie.controller;

import com.foodie.entity.Restaurant;
import com.foodie.service.RestaurantService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/restaurants")
@Tag(name = "Restaurants")
public class RestaurantController {
    private final RestaurantService service;

    public RestaurantController(RestaurantService service) { this.service = service; }

    @GetMapping
    public List<Restaurant> all() { return service.findAll(); }

    @GetMapping("/{id}")
    public Restaurant one(@PathVariable Long id) { return service.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Restaurant create(@RequestBody Restaurant r) { return service.create(r); }

    @PutMapping("/{id}")
    public Restaurant update(@PathVariable Long id, @RequestBody Restaurant r) { return service.update(id, r); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}
