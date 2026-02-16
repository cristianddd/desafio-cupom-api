package com.project.couponservice.application;

import com.project.couponservice.application.get.GetCouponCommand;
import com.project.couponservice.application.get.GetCouponService;
import com.project.couponservice.domain.Coupon;
import com.project.couponservice.domain.NotFoundException;
import com.project.couponservice.domain.ports.CouponPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GetCouponUseCaseTest {
    @Test
    void executeShouldReturnCouponDetailsWhenCouponExists() {
        CouponPort couponPort = mock(CouponPort.class);
        GetCouponService service = new GetCouponService(couponPort);

        Coupon coupon = Coupon.with(
                1L,
                "ABC123",
                "Cupom de teste",
                BigDecimal.valueOf(0.8),
                LocalDateTime.now().plusDays(2),
                true,
                false,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(1)
        );

        when(couponPort.findById(1L)).thenReturn(Optional.of(coupon));

        var output = service.execute(new GetCouponCommand(1L));

        assertNotNull(output);
        assertEquals(1L, output.id());
        assertEquals("ABC123", output.code());
        assertEquals("Cupom de teste", output.description());
        assertEquals(BigDecimal.valueOf(0.8), output.discountValue());
        assertEquals("ACTIVE", output.status());
        assertTrue(output.published());
        assertFalse(output.redeemed());
        verify(couponPort, times(1)).findById(1L);
    }

    @Test
    void executeShouldThrowWhenCouponDoesNotExist() {
        CouponPort couponPort = mock(CouponPort.class);
        GetCouponService service = new GetCouponService(couponPort);

        when(couponPort.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.execute(new GetCouponCommand(99L)));
        verify(couponPort, times(1)).findById(99L);
    }
}
