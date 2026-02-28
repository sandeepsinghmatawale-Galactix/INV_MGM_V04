package com.barinventory.billing.dtos;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrandBillingDTO {

    private Long id;
    private String name;
    private List<BrandSizeDTO> sizes;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BrandSizeDTO {
        private Long id;
        private String sizeLabel;
        private BigDecimal price;
        private boolean active;
    }
}