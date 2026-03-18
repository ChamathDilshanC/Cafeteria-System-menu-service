package com.cafeteria.menuservice.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemResponse {
  private Long id;
  private String name;
  private String description;
  private BigDecimal price;
  private String imageUrl;
  private boolean available;
  private Long categoryId;
  private String categoryName;
}
