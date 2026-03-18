package com.cafeteria.menuservice.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cafeteria.menuservice.dto.MenuItemRequest;
import com.cafeteria.menuservice.dto.MenuItemResponse;
import com.cafeteria.menuservice.service.MenuService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/menu")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    public ResponseEntity<List<MenuItemResponse>> getItems(
            @RequestParam(required = false) Long categoryId) {
        return ResponseEntity.ok(menuService.getAvailableItems(categoryId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItemResponse> getItem(@PathVariable Long id) {
        return ResponseEntity.ok(menuService.getItemById(id));
    }

    /**
     * Creates a menu item. Image is optional; send as multipart/form-data.
     * Example cURL:
     * curl -X POST http://localhost:8080/api/menu \
     * -F "data={...json...};type=application/json" \
     * -F "image=@burger.jpg"
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MenuItemResponse> createItem(
            @Valid @RequestPart("data") MenuItemRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(menuService.createItem(request, image));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MenuItemResponse> updateItem(
            @PathVariable Long id,
            @Valid @RequestPart("data") MenuItemRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        return ResponseEntity.ok(menuService.updateItem(id, request, image));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        menuService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}
