package com.cafeteria.menuservice.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemRequest {

  @NotBlank
  private String name;

  private String description;

  @NotNull
  @DecimalMin("0.01")
  private BigDecimal price;

  @NotNull
  private Long categoryId;

  @Builder.Default
  private boolean available = true;
}
