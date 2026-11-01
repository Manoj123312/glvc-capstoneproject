package com.foodie.repository;

import com.foodie.entity.OrderEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<OrderEntity> findAllByOrderByCreatedAtDesc();
}
