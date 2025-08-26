package com.zara.ecommerce.controllers;

import com.zara.ecommerce.dto.PriceDto;
import com.zara.ecommerce.responses.PriceResponse;
import com.zara.ecommerce.services.PriceService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/v1/prices")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Price", description = "Applicable price lookup")
public class PriceController {

    private final PriceService service;

    public PriceController(PriceService service) {
        this.service = service;
    }

    @Operation(
            summary = "Get applicable price",
            description = "Returns the single applicable price for the given brandId, productId and date."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Price found"),
            @ApiResponse(responseCode = "400", description = "Invalid or missing parameters"),
            @ApiResponse(responseCode = "404", description = "No applicable price found")
    })
    @GetMapping(produces = "application/json")
    public ResponseEntity<PriceResponse> getApplicablePrice(
            @Valid @ParameterObject @ModelAttribute PriceDto query
    ) {
        return service.getApplicablePrice(query.getBrandId(), query.getProductId(), query.getDate())
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "No applicable price found"));
    }
}