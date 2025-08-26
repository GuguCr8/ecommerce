package com.zara.ecommerce.services;

import com.zara.ecommerce.responses.PriceResponse;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PriceService {

    public Optional<PriceResponse> getApplicablePrice(int brandId, long productId, LocalDateTime date);
}