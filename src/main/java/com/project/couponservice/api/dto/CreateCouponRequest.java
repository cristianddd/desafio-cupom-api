package com.project.couponservice.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CreateCouponRequest {

    @NotBlank
    private String code;

    @NotBlank
    private String description;

    @NotNull
    @DecimalMin(value = "0.5", message = "O valor do desconto deve ser no mínimo 0,5")
    private BigDecimal discountValue;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expirationDate;

    private boolean published;

}