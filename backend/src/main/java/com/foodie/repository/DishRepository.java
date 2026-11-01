package com.foodie.repository;

import com.foodie.entity.Dish;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DishRepository extends JpaRepository<Dish, Long> {
    @Query("""
        select d from Dish d
        where (:restaurantId is null or d.restaurant.id = :restaurantId)
          and (:category is null or lower(d.category) = lower(:category))
          and (:veg is null or d.veg = :veg)
          and (:maxPrice is null or d.price <= :maxPrice)
          and (:q is null or lower(d.name) like lower(concat('%', :q, '%')))
        order by d.name
        """)
    List<Dish> search(@Param("restaurantId") Long restaurantId,
                      @Param("category") String category,
                      @Param("veg") Boolean veg,
                      @Param("maxPrice") BigDecimal maxPrice,
                      @Param("q") String q);

    long countByRestaurantId(Long restaurantId);
}
