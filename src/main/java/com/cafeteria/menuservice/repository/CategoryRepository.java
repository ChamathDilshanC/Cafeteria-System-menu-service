package com.cafeteria.menuservice.repository;

import com.cafeteria.menuservice.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByActiveTrue();
}
