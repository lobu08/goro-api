package com.goro.goro.api.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.goro.goro.api.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByName(String name);
}