package com.project.couponservice.domain;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;

@Getter
@Builder
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
        LocalDateTime now = LocalDateTime.now();
        validateCouponCreation(sanitizedCode, discountValue, expirationDate, now);

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

    public void redeem() {
        validateRedeem(LocalDateTime.now());
        this.published = true;
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
        ensureNotDeleted("O cupom já foi excluído.");
        this.deleted = true;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isExpired(LocalDateTime referenceDateTime) {
        return !expirationDate.isAfter(referenceDateTime);
    }

    private static void validateCouponCreation(String sanitizedCode,
                                               BigDecimal discountValue,
                                               LocalDateTime expirationDate,
                                               LocalDateTime now) {
        ensureValidCodeLength(sanitizedCode);
        ensureMinimumDiscount(discountValue);
        ensureNotExpired(expirationDate, now,
                "A data de validade deve ser maior que o dia de hoje.");
    }

    private void validateRedeem(LocalDateTime now) {
        ensureNotDeleted("O coupon foi delatado!");
        ensurePublished();
        ensureNotExpired(this.expirationDate, now,
                "A data de validade do cupom deve ser maior que o dia de hoje.");
    }

    private static void ensureValidCodeLength(String sanitizedCode) {
        if (sanitizedCode.length() != 6) {
            throw new DomainException("O código do cupom deve ser alfanumérico e ter exatamente 6 caracteres.");
        }
    }

    private static void ensureMinimumDiscount(BigDecimal discountValue) {
        if (discountValue.compareTo(BigDecimal.valueOf(0.5)) < 0) {
            throw new DomainException("O valor do desconto deve ser de pelo menos 0,5.");
        }
    }

    private static void ensureNotExpired(LocalDateTime expirationDate,
                                         LocalDateTime now,
                                         String message) {
        if (!expirationDate.isAfter(now)) {
            throw new DomainException(message);
        }
    }

    private void ensureNotDeleted(String message) {
        if (this.deleted) {
            throw new DomainException(message);
        }
    }

    private void ensurePublished() {
        if (!this.published) {
            throw new DomainException("O coupon não esta mais publicado!");
        }
    }

}
