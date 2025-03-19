package com.goro.goro.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.goro.goro.api.model.Category;
import com.goro.goro.api.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContaining(String name);
    List<Product> findByCategory(Category category);
}
