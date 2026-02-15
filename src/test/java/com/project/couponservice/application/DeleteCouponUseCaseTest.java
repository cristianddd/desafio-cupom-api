package com.project.couponservice.application;

import com.project.couponservice.application.delete.DeleteCouponCommand;
import com.project.couponservice.application.delete.DeleteCouponService;
import com.project.couponservice.domain.Coupon;
import com.project.couponservice.domain.DomainException;
import com.project.couponservice.domain.NotFoundException;
import com.project.couponservice.domain.ports.CouponPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DeleteCouponUseCaseTest {

    @Test
    void executeShouldSoftDeleteCoupon() {
        CouponPort gateway = mock(CouponPort.class);
        DeleteCouponService service = new DeleteCouponService(gateway);

        Coupon existing = Coupon.newCoupon(
                "ABCD12",
                "Desc",
                BigDecimal.ONE,
                LocalDateTime.now().plusDays(1),
                false
        );

        Coupon persisted = Coupon.with(
                10L,
                existing.getCode(),
                existing.getDescription(),
                existing.getDiscountValue(),
                existing.getExpirationDate(),
                existing.isPublished(),
                false,
                existing.getCreatedAt(),
                existing.getUpdatedAt()
        );

        when(gateway.findById(10L)).thenReturn(Optional.of(persisted));
        Coupon deleted = Coupon.with(
                10L,
                persisted.getCode(),
                persisted.getDescription(),
                persisted.getDiscountValue(),
                persisted.getExpirationDate(),
                persisted.isPublished(),
                true,
                persisted.getCreatedAt(),
                persisted.getUpdatedAt()
        );
        when(gateway.update(any(Coupon.class))).thenReturn(deleted);

        var output = service.execute(new DeleteCouponCommand(10L));
        assertNotNull(output);
        assertEquals(10L, output.id());
        verify(gateway, times(1)).findById(10L);
        verify(gateway, times(1)).update(any(Coupon.class));
    }

    @Test
    void executeShouldThrowWhenCouponNotFound() {
        CouponPort gateway = mock(CouponPort.class);
        DeleteCouponService service = new DeleteCouponService(gateway);

        when(gateway.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.execute(new DeleteCouponCommand(1L)));
        verify(gateway, times(1)).findById(1L);
        verify(gateway, never()).update(any());
    }

    @Test
    void executeShouldThrowWhenAlreadyDeleted() {
        CouponPort gateway = mock(CouponPort.class);
        DeleteCouponService service = new DeleteCouponService(gateway);

        Coupon deletedCoupon = Coupon.newCoupon(
                "ABCD12",
                "Desc",
                BigDecimal.ONE,
                LocalDateTime.now().plusDays(1),
                false
        );

        Coupon persistedDeleted = Coupon.with(
                5L,
                deletedCoupon.getCode(),
                deletedCoupon.getDescription(),
                deletedCoupon.getDiscountValue(),
                deletedCoupon.getExpirationDate(),
                deletedCoupon.isPublished(),
                true,
                deletedCoupon.getCreatedAt(),
                deletedCoupon.getUpdatedAt()
        );

        when(gateway.findById(5L)).thenReturn(Optional.of(persistedDeleted));
        assertThrows(DomainException.class, () -> service.execute(new DeleteCouponCommand(5L)));
        verify(gateway, times(1)).findById(5L);
        verify(gateway, never()).update(any());
    }
}