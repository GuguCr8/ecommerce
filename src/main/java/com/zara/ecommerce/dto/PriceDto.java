package com.zara.ecommerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class PriceDto {

    @NotNull(message = "brandId is required")
    @Positive(message = "brandId must be > 0")
    @Schema(example = "1", description = "Brand id (1 = ZARA)", minimum = "1")
    private Integer brandId;

    @NotNull(message = "productId is required")
    @Positive(message = "productId must be > 0")
    @Schema(example = "35455", description = "Product id", minimum = "1")
    private Long productId;

    @NotNull(message = "date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Schema(
            example = "2020-06-14T16:00:00",
            description = "Application date-time (ISO-8601, no timezone). " +
                    "If you need 'Z' (UTC), switch to OffsetDateTime."
    )
    private LocalDateTime date;
}
