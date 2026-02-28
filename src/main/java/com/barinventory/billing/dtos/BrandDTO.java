package com.barinventory.billing.dtos;

import java.util.List;

import com.barinventory.billing.entity.Brand;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandDTO {
	private Long id;
	private String name;
	private String parentCompany;
	private Brand.Category category;
	private String exciseCode;
	private boolean active;
	private List<BrandSizeDTO> sizes;
}