package com.cafeteria.menuservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cafeteria.menuservice.dto.CategoryRequest;
import com.cafeteria.menuservice.entity.Category;
import com.cafeteria.menuservice.repository.CategoryRepository;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getActiveCategories() {
        return categoryRepository.findAllByActiveTrue();
    }

    public Category createCategory(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found: " + id));
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found: " + id));
        category.setActive(false); // soft-delete
        categoryRepository.save(category);
    }
}
