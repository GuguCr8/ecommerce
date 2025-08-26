package com.zara.ecommerce.services.impl;


import com.zara.ecommerce.entities.Prices;
import com.zara.ecommerce.repositories.PriceJpaRepository;
import com.zara.ecommerce.responses.PriceResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PriceServiceImpl {

    private final PriceJpaRepository repository;

    public PriceServiceImpl(PriceJpaRepository repository) {
        this.repository = repository;
    }

    public Optional<PriceResponse> getApplicablePrice(int brandId, long productId, LocalDateTime date) {
        return repository
                .findTop1ByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDescPriceListDesc(
                        brandId, productId, date, date
                )
                .map(this::toDto);
    }

    private PriceResponse toDto(Prices prices) {
        return new PriceResponse(prices.getProductId(), prices.getBrandId(), prices.getPriceList(), prices.getStartDate(), prices.getEndDate(), prices.getPrice(), prices.getCurrency()
        );
    }
}
