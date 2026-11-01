package com.foodie.controller;

import com.foodie.dto.Dtos.OrderDto;
import com.foodie.dto.Dtos.OrderRequest;
import com.foodie.dto.Dtos.StatusRequest;
import com.foodie.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Orders")
public class OrderController {
    private final OrderService service;

    public OrderController(OrderService service) { this.service = service; }

    @PostMapping("/api/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto place(Principal p, @Valid @RequestBody OrderRequest req) { return service.placeOrder(p.getName(), req); }

    @GetMapping("/api/orders/my")
    public List<OrderDto> mine(Principal p) { return service.myOrders(p.getName()); }

    @PutMapping("/api/orders/{id}/cancel")
    public OrderDto cancel(Principal p, @PathVariable Long id) { return service.cancel(p.getName(), id); }

    @GetMapping("/api/admin/orders")
    public List<OrderDto> all() { return service.allOrders(); }

    @PutMapping("/api/admin/orders/{id}/status")
    public OrderDto updateStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest req) {
        return service.updateStatus(id, req.status());
    }
}
