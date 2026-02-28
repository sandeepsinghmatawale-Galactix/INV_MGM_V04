package com.barinventory.billing.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillResponse {
	private Long billId;
	private String createdBy;
	private LocalDateTime createdAt;
	private BigDecimal grandTotal;
	private boolean finalized;
	private List<BillItemResponse> items;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class BillItemResponse {
		private Long brandId;
		private String brandName;
		private String sizeLabel;
		private BigDecimal unitPrice;
		private Integer quantity;
		private BigDecimal lineTotal;
	}
}