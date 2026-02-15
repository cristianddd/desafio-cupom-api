package com.project.couponservice.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;

@Data
public class Coupon {

    private Long id;
    private String code;
    private String description;
    private BigDecimal discountValue;
    private LocalDateTime expirationDate;
    private boolean published;
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Coupon(Long id,
                  String code,
                  String description,
                  BigDecimal discountValue,
                  LocalDateTime expirationDate,
                  boolean published,
                  boolean deleted,
                  LocalDateTime createdAt,
                  LocalDateTime updatedAt) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.discountValue = discountValue;
        this.expirationDate = expirationDate;
        this.published = published;
        this.deleted = deleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Coupon newCoupon(String rawCode,
                                   String description,
                                   BigDecimal discountValue,
                                   LocalDateTime expirationDate,
                                   boolean published) {
        Objects.requireNonNull(rawCode, "O campo codigo não pode ser nulo");
        Objects.requireNonNull(description, "O campo descricao não pode ser nulo");
        Objects.requireNonNull(discountValue, "O campo valor desconto não pode ser nulo");
        Objects.requireNonNull(expirationDate, "O campo valor data expiracao não pode ser nulo");

        String sanitizedCode = sanitizeCode(rawCode);
        if (sanitizedCode.length() != 6) {
            throw new DomainException("O código do cupom deve ser alfanumérico e ter exatamente 6 caracteres.");
        }
        if (discountValue.compareTo(BigDecimal.valueOf(0.5)) < 0) {
            throw new DomainException("O valor do desconto deve ser de pelo menos 0,5.");
        }
        if (!expirationDate.isAfter(LocalDateTime.now())) {
            throw new DomainException("A data de validade deve ser maior que o dia de hoje.");
        }
        LocalDateTime now = LocalDateTime.now();
        return new Coupon(
                null,
                sanitizedCode.toUpperCase(Locale.ROOT),
                description,
                discountValue,
                expirationDate,
                published,
                false,
                now,
                now
        );
    }

    public static Coupon with(Long id,
                              String code,
                              String description,
                              BigDecimal discountValue,
                              LocalDateTime expirationDate,
                              boolean published,
                              boolean deleted,
                              LocalDateTime createdAt,
                              LocalDateTime updatedAt) {
        return new Coupon(id, code, description, discountValue, expirationDate, published, deleted, createdAt, updatedAt);
    }

    private static String sanitizeCode(String rawCode) {
        StringBuilder builder = new StringBuilder();
        for (char c : rawCode.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    public void delete() {
        if (this.deleted) {
            throw new DomainException("O cupom já foi excluído.");
        }
        this.deleted = true;
        this.updatedAt = LocalDateTime.now();
    }
}