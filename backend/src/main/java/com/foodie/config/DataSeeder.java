package com.foodie.config;

import com.foodie.entity.Dish;
import com.foodie.entity.Restaurant;
import com.foodie.entity.User;
import com.foodie.repository.DishRepository;
import com.foodie.repository.RestaurantRepository;
import com.foodie.repository.UserRepository;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seed(UserRepository users, RestaurantRepository restaurants, DishRepository dishes, PasswordEncoder enc) {
        return args -> {
            if (!users.existsByEmail("admin@foodie.com")) {
                users.save(user("Admin", "admin@foodie.com", "admin123", User.Role.ADMIN, enc));
                users.save(user("Demo User", "user@foodie.com", "user123", User.Role.USER, enc));
            }
            if (restaurants.count() == 0) {
                Restaurant r1 = restaurant(restaurants, "Spice Garden", "Indian", "Hyderabad", "Banjara Hills", 4.5,
                        "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=600");
                Restaurant r2 = restaurant(restaurants, "Pizza Palace", "Italian", "Hyderabad", "Madhapur", 4.2,
                        "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=600");
                Restaurant r3 = restaurant(restaurants, "Dragon Wok", "Chinese", "Hyderabad", "Gachibowli", 4.0,
                        "https://images.unsplash.com/photo-1525755662778-989d0524087e?w=600");
                dish(dishes, r1, "Chicken Biryani", "Hyderabadi dum biryani", "249", "Main Course", false, "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=600");
                dish(dishes, r1, "Paneer Butter Masala", "Creamy tomato gravy", "199", "Main Course", true, "https://images.unsplash.com/photo-1631452180519-c014fe946bc7?w=600");
                dish(dishes, r1, "Gulab Jamun", "Sweet milk dumplings", "89", "Dessert", true, "https://images.unsplash.com/photo-1666190077424-8c6d5c3d3f7e?w=600");
                dish(dishes, r2, "Margherita Pizza", "Classic cheese pizza", "299", "Pizza", true, "https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=600");
                dish(dishes, r2, "Pepperoni Pizza", "Spicy pepperoni", "379", "Pizza", false, "https://images.unsplash.com/photo-1628840042765-356cda07504e?w=600");
                dish(dishes, r2, "Garlic Bread", "Toasted with butter", "119", "Starter", true, "https://images.unsplash.com/photo-1573140401552-3fab0b24306f?w=600");
                dish(dishes, r3, "Veg Hakka Noodles", "Wok tossed noodles", "169", "Noodles", true, "https://images.unsplash.com/photo-1585032226651-759b368d7246?w=600");
                dish(dishes, r3, "Chicken Manchurian", "Indo-Chinese gravy", "229", "Main Course", false, "https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=600");
            }
        };
    }

    private User user(String name, String email, String pw, User.Role role, PasswordEncoder enc) {
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setPassword(enc.encode(pw));
        u.setRole(role);
        return u;
    }

    private Restaurant restaurant(RestaurantRepository repo, String name, String cuisine, String city, String address,
                                  double rating, String img) {
        Restaurant r = new Restaurant();
        r.setName(name);
        r.setCuisine(cuisine);
        r.setCity(city);
        r.setAddress(address);
        r.setRating(rating);
        r.setImageUrl(img);
        return repo.save(r);
    }

    private void dish(DishRepository repo, Restaurant r, String name, String desc, String price, String cat,
                      boolean veg, String img) {
        Dish d = new Dish();
        d.setRestaurant(r);
        d.setName(name);
        d.setDescription(desc);
        d.setPrice(new BigDecimal(price));
        d.setCategory(cat);
        d.setVeg(veg);
        d.setImageUrl(img);
        repo.save(d);
    }
}
