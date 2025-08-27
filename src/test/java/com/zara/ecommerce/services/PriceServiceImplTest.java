package com.zara.ecommerce.services;

import com.zara.ecommerce.entities.Prices;
import com.zara.ecommerce.repositories.PriceJpaRepository;
import com.zara.ecommerce.responses.PriceResponse;
import com.zara.ecommerce.services.impl.PriceServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PriceServiceImpl Tests")
class PriceServiceImplTest {

    @Mock
    private PriceJpaRepository repository;

    @InjectMocks
    private PriceServiceImpl service;

    private Prices entity;
    private LocalDateTime start;
    private LocalDateTime end;
    private LocalDateTime query;

    @BeforeEach
    void setUp() {
        start = LocalDateTime.parse("2020-06-14T15:00:00");
        end   = LocalDateTime.parse("2020-06-14T18:30:00");
        query = LocalDateTime.parse("2020-06-14T16:00:00");

        entity = Prices.builder().id(100L).brandId(1).productId(35455L).priceList(2).startDate(start).endDate(end)
                .priority(1).price(new BigDecimal("25.45")).currency("EUR").build();
    }

    @Test
    @DisplayName("Must return mapped PriceResponse when repository finds price")
    void getApplicablePrice_WhenFound_ShouldReturnMappedResponse() {

        when(repository.findTop1ByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDescPriceListDesc(
                1, 35455L, query, query)).thenReturn(Optional.of(entity));

        Optional<PriceResponse> out = service.getApplicablePrice(1, 35455L, query);

        assertTrue(out.isPresent());
        PriceResponse r = out.get();
        assertEquals(35455L, r.productId());
        assertEquals(1, r.brandId());
        assertEquals(2, r.priceList());
        assertEquals(start, r.startDate());
        assertEquals(end, r.endDate());
        assertEquals(0, r.price().compareTo(new BigDecimal("25.45")));
        assertEquals("EUR", r.currency());

        verify(repository, times(1))
                .findTop1ByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDescPriceListDesc(
                        1, 35455L, query, query);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Should return Optional.empty when no price is applicable")
    void getApplicablePrice_WhenNotFound_ShouldReturnEmpty() {

        when(repository.findTop1ByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDescPriceListDesc(
                1, 35455L, query, query)).thenReturn(Optional.empty());

        Optional<PriceResponse> out = service.getApplicablePrice(1, 35455L, query);

        assertTrue(out.isEmpty());
        verify(repository, times(1))
                .findTop1ByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDescPriceListDesc(
                        1, 35455L, query, query);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("You must invoke the repository with the correct parameters")
    void getApplicablePrice_ShouldCallRepositoryWithCorrectParams() {

        when(repository.findTop1ByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDescPriceListDesc(
                anyInt(), anyLong(), any(), any())).thenReturn(Optional.of(entity));

        service.getApplicablePrice(1, 35455L, query);

        verify(repository, times(1))
                .findTop1ByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDescPriceListDesc(
                        eq(1), eq(35455L), eq(query), eq(query));
        verifyNoMoreInteractions(repository);
    }
}
