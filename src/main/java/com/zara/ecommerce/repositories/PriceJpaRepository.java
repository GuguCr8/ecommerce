package com.zara.ecommerce.repositories;

import com.zara.ecommerce.entities.Prices;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PriceJpaRepository extends JpaRepository<Prices, Long> {

    Optional<Prices>
    findTop1ByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDescPriceListDesc(
            int brandId, long productId, LocalDateTime date1, LocalDateTime date2
    );
}
