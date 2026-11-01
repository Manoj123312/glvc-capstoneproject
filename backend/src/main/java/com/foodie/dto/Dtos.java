package com.foodie.dto;

import com.foodie.entity.OrderEntity;
import com.foodie.entity.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public final class Dtos {
    private Dtos() {}

    public record RegisterRequest(@NotBlank String name, @NotBlank @Email String email,
                                  @NotBlank @Size(min = 6) String password, String phone, String address) {}

    public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {}

    public record AuthResponse(String token, Long id, String name, String email, String role) {}

    public record UserDto(Long id, String name, String email, String phone, String address, String role, boolean active) {
        public static UserDto from(User u) {
            return new UserDto(u.getId(), u.getName(), u.getEmail(), u.getPhone(), u.getAddress(),
                    u.getRole().name(), u.isActive());
        }
    }

    public record UserUpdateRequest(@NotBlank String name, String phone, String address, User.Role role, Boolean active) {}

    public record DishRequest(@NotNull Long restaurantId, @NotBlank String name, String description,
                              @NotNull @DecimalMin("0.0") BigDecimal price, String category, boolean veg,
                              String imageUrl, Boolean available) {}

    public record OrderItemRequest(@NotNull Long dishId, @Min(1) int quantity) {}

    public record OrderRequest(@NotBlank String deliveryAddress, @NotEmpty @Valid List<OrderItemRequest> items) {}

    public record StatusRequest(@NotNull OrderEntity.Status status) {}

    public record OrderItemDto(Long dishId, String dishName, String restaurantName, BigDecimal unitPrice, int quantity) {}

    public record OrderDto(Long id, Long userId, String userName, String status, BigDecimal totalAmount,
                           String deliveryAddress, LocalDateTime createdAt, List<OrderItemDto> items) {
        public static OrderDto from(OrderEntity o) {
            return new OrderDto(o.getId(), o.getUser().getId(), o.getUser().getName(), o.getStatus().name(),
                    o.getTotalAmount(), o.getDeliveryAddress(), o.getCreatedAt(),
                    o.getItems().stream().map(i -> new OrderItemDto(i.getDishId(), i.getDishName(),
                            i.getRestaurantName(), i.getUnitPrice(), i.getQuantity())).toList());
        }
    }
}
