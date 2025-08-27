package com.zara.ecommerce.controllers;

import com.zara.ecommerce.responses.PriceResponse;
import com.zara.ecommerce.services.PriceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;


import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PriceController.class)
@DisplayName("PriceController (Web layer) Tests")
class PriceControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    PriceService service;

    private static final String URL = "/api/v1/prices";

    @Test
    @DisplayName("200 OK → returns the applicable price")
    void getApplicablePrice_ok() throws Exception {
        var start = LocalDateTime.parse("2020-06-14T15:00:00");
        var end   = LocalDateTime.parse("2020-06-14T18:30:00");
        var date  = LocalDateTime.parse("2020-06-14T16:00:00");

        var resp = new PriceResponse(
                35455L, 1, 2, start, end, new BigDecimal("25.45"), "EUR"
        );

        when(service.getApplicablePrice(eq(1), eq(35455L), eq(date)))
                .thenReturn(Optional.of(resp));

        mockMvc.perform(get(URL).param("brandId", "1").param("productId", "35455").param("date", "2020-06-14T16:00:00"))

                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(2))
                .andExpect(jsonPath("$.startDate").value("2020-06-14T15:00:00"))
                .andExpect(jsonPath("$.endDate").value("2020-06-14T18:30:00"))
                .andExpect(jsonPath("$.price").value(25.45))
                .andExpect(jsonPath("$.currency").value("EUR"));
    }

    @Test
    @DisplayName("404 Not Found → no applicable price")
    void getApplicablePrice_notFound() throws Exception {
        var date = "2030-01-01T00:00:00";
        when(service.getApplicablePrice(eq(1), eq(35455L), eq(LocalDateTime.parse(date))))
                .thenReturn(Optional.empty());

        mockMvc.perform(get(URL).param("brandId", "1").param("productId", "35455").param("date", date))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("400 Bad Request → missing mandatory parameter (date)")
    void getApplicablePrice_missingDate() throws Exception {
        mockMvc.perform(get(URL).param("brandId", "1").param("productId", "35455")).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("400 Bad Request → invalid date format")
    void getApplicablePrice_invalidDateFormat() throws Exception {

        mockMvc.perform(get(URL).param("brandId", "1").param("productId", "35455").param("date", "2020-06-14 16:00:00"))
                .andExpect(status().isBadRequest());
    }
}
