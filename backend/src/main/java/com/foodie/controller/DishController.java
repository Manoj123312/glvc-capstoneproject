package com.foodie.controller;

import com.foodie.dto.Dtos.DishRequest;
import com.foodie.entity.Dish;
import com.foodie.service.DishService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dishes")
@Tag(name = "Dishes")
public class DishController {
    private final DishService service;

    public DishController(DishService service) { this.service = service; }

    @GetMapping
    public List<Dish> search(@RequestParam(required = false) Long restaurantId,
                             @RequestParam(required = false) String category,
                             @RequestParam(required = false) Boolean veg,
                             @RequestParam(required = false) BigDecimal maxPrice,
                             @RequestParam(required = false) String q) {
        return service.search(restaurantId, category, veg, maxPrice, q);
    }

    @GetMapping("/{id}")
    public Dish one(@PathVariable Long id) { return service.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Dish create(@Valid @RequestBody DishRequest req) { return service.create(req); }

    @PutMapping("/{id}")
    public Dish update(@PathVariable Long id, @Valid @RequestBody DishRequest req) { return service.update(id, req); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}
