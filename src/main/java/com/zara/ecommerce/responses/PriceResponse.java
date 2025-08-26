package com.zara.ecommerce.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PriceResponse(long productId, int brandId, int priceList, LocalDateTime startDate, LocalDateTime endDate,
                            BigDecimal price, String currency) {
}
