package com.barinventory.billing.dtos;

import java.math.BigDecimal;

import com.barinventory.billing.entity.BrandSize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandSizeDTO {
	private Long id;
	private String sizeLabel;
	private BigDecimal price;
	private BrandSize.Packaging packaging;
	private Double abvPercent;
	private Integer displayOrder;
	private boolean active;
}