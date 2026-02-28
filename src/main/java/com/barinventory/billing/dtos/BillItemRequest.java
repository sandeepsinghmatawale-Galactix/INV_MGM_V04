package com.barinventory.billing.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillItemRequest {

	@NotNull
	private Long brandSizeId; // we fetch brand + size + price from here

	@NotNull
	@Min(1)
	private Integer quantity;
}