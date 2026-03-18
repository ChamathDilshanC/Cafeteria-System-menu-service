package com.cafeteria.menuservice.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cafeteria.menuservice.dto.MenuItemRequest;
import com.cafeteria.menuservice.dto.MenuItemResponse;
import com.cafeteria.menuservice.entity.Category;
import com.cafeteria.menuservice.entity.MenuItem;
import com.cafeteria.menuservice.repository.CategoryRepository;
import com.cafeteria.menuservice.repository.MenuItemRepository;

@Service
public class MenuService {

    private final MenuItemRepository menuItemRepository;
    private final CategoryRepository categoryRepository;
    private final FileService fileService;

    public MenuService(MenuItemRepository menuItemRepository, CategoryRepository categoryRepository,
            FileService fileService) {
        this.menuItemRepository = menuItemRepository;
        this.categoryRepository = categoryRepository;
        this.fileService = fileService;
    }

    public List<MenuItemResponse> getAvailableItems(Long categoryId) {
        List<MenuItem> items = categoryId != null
                ? menuItemRepository.findByCategoryIdAndAvailableTrue(categoryId)
                : menuItemRepository.findAllByAvailableTrue();
        return items.stream().map(this::toResponse).toList();
    }

    public MenuItemResponse getItemById(Long id) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found: " + id));
        return toResponse(item);
    }

    public MenuItemResponse createItem(MenuItemRequest request, MultipartFile image) throws IOException {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found: " + request.getCategoryId()));

        MenuItem item = new MenuItem();
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setAvailable(request.isAvailable());
        item.setCategory(category);

        if (image != null && !image.isEmpty()) {
            item.setImageUrl(fileService.uploadImage(image));
        }

        return toResponse(menuItemRepository.save(item));
    }

    public MenuItemResponse updateItem(Long id, MenuItemRequest request, MultipartFile image) throws IOException {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found: " + id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found: " + request.getCategoryId()));

        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setAvailable(request.isAvailable());
        item.setCategory(category);

        if (image != null && !image.isEmpty()) {
            fileService.deleteImage(item.getImageUrl());
            item.setImageUrl(fileService.uploadImage(image));
        }

        return toResponse(menuItemRepository.save(item));
    }

    public void deleteItem(Long id) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found: " + id));
        fileService.deleteImage(item.getImageUrl());
        menuItemRepository.delete(item);
    }

    private MenuItemResponse toResponse(MenuItem item) {
        MenuItemResponse r = new MenuItemResponse();
        r.setId(item.getId());
        r.setName(item.getName());
        r.setDescription(item.getDescription());
        r.setPrice(item.getPrice());
        r.setImageUrl(item.getImageUrl());
        r.setAvailable(item.isAvailable());
        r.setCategoryId(item.getCategory().getId());
        r.setCategoryName(item.getCategory().getName());
        return r;
    }
}
