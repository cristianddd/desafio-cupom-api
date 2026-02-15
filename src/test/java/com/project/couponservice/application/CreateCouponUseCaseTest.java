package com.project.couponservice.application;

import com.project.couponservice.application.create.CreateCouponCommand;
import com.project.couponservice.application.create.CreateCouponOutput;
import com.project.couponservice.application.create.CreateCouponService;
import com.project.couponservice.domain.Coupon;
import com.project.couponservice.domain.ports.CouponPort;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreateCouponUseCaseTest {

    @Test
    void executeShouldPersistCouponAndReturnOutput() {
        CouponPort gateway = mock(CouponPort.class);
        CreateCouponService service = new CreateCouponService(gateway);

        CreateCouponCommand command = new CreateCouponCommand(
                "AB#CD12",
                "Test description",
                BigDecimal.ONE,
                LocalDateTime.now().plusDays(1),
                true
        );

        // Prepare saved entity to return from gateway
        Coupon prePersist = Coupon.newCoupon(
                command.code(),
                command.description(),
                command.discountValue(),
                command.expirationDate(),
                command.published()
        );
        Coupon saved = Coupon.with(
                1L,
                prePersist.getCode(),
                prePersist.getDescription(),
                prePersist.getDiscountValue(),
                prePersist.getExpirationDate(),
                prePersist.isPublished(),
                prePersist.isDeleted(),
                prePersist.getCreatedAt(),
                prePersist.getUpdatedAt()
        );
        when(gateway.save(any(Coupon.class))).thenReturn(saved);

        CreateCouponOutput output = service.execute(command);

        assertNotNull(output);
        assertEquals(1L, output.id());
        assertEquals(prePersist.getCode(), output.code());
        assertEquals(prePersist.getExpirationDate(), output.expirationDate());

        ArgumentCaptor<Coupon> captor = ArgumentCaptor.forClass(Coupon.class);
        verify(gateway, times(1)).save(captor.capture());
        Coupon passedCoupon = captor.getValue();
        assertEquals("ABCD12", passedCoupon.getCode());
    }
}