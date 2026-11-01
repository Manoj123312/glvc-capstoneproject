-- Reference schema for the Foodie database (MySQL 8).
-- NOTE: The Spring Boot app creates/updates these tables automatically (spring.jpa.hibernate.ddl-auto=update)
-- and seeds an admin user + sample restaurants/dishes on first start. Run this file only if you prefer manual setup.

CREATE DATABASE IF NOT EXISTS foodie_db;
USE foodie_db;

CREATE TABLE IF NOT EXISTS users (
  id        BIGINT AUTO_INCREMENT PRIMARY KEY,
  name      VARCHAR(255) NOT NULL,
  email     VARCHAR(255) NOT NULL UNIQUE,
  password  VARCHAR(255) NOT NULL,          -- BCrypt hash
  phone     VARCHAR(255),
  address   VARCHAR(255),
  role      ENUM('ADMIN','USER') NOT NULL DEFAULT 'USER',
  active    BIT NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS restaurants (
  id        BIGINT AUTO_INCREMENT PRIMARY KEY,
  name      VARCHAR(255) NOT NULL,
  cuisine   VARCHAR(255),
  city      VARCHAR(255),
  address   VARCHAR(255),
  image_url VARCHAR(255),
  rating    DOUBLE NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS dishes (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  name          VARCHAR(255) NOT NULL,
  description   VARCHAR(500),
  price         DECIMAL(38,2) NOT NULL,
  category      VARCHAR(255),
  veg           BIT NOT NULL DEFAULT 0,
  image_url     VARCHAR(255),
  available     BIT NOT NULL DEFAULT 1,
  restaurant_id BIGINT NOT NULL,
  CONSTRAINT fk_dish_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
);

CREATE TABLE IF NOT EXISTS orders (
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id          BIGINT NOT NULL,
  status           ENUM('PLACED','CONFIRMED','PREPARING','OUT_FOR_DELIVERY','DELIVERED','CANCELLED') NOT NULL,
  total_amount     DECIMAL(38,2) NOT NULL,
  delivery_address VARCHAR(255),
  created_at       DATETIME(6),
  CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS order_items (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id        BIGINT NOT NULL,
  dish_id         BIGINT,
  dish_name       VARCHAR(255),
  restaurant_name VARCHAR(255),
  unit_price      DECIMAL(38,2),
  quantity        INT NOT NULL,
  CONSTRAINT fk_item_order FOREIGN KEY (order_id) REFERENCES orders(id)
);
