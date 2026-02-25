package com.project.couponservice.application;

import com.project.couponservice.application.create.CreateCouponCommand;
import com.project.couponservice.application.create.CreateCouponService;
import com.project.couponservice.application.get.GetCouponCommand;
import com.project.couponservice.application.get.GetCouponService;
import com.project.couponservice.domain.NotFoundException;
import com.project.couponservice.infra.entity.CouponJpaEntity;
import com.project.couponservice.infra.repository.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GetCouponUseCaseTest {

    @Autowired
    private GetCouponService service;

    @Autowired
    private CreateCouponService createCouponService;

    @Autowired
    private CouponRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void executeShouldReturnCouponDetailsWhenCouponExists() {
        var created = createCouponService.execute(new CreateCouponCommand(
                "ABC123",
                "Cupom de teste",
                BigDecimal.valueOf(0.8),
                LocalDateTime.now().plusDays(2),
                true
        ));

        var output = service.execute(new GetCouponCommand(created.id()));

        assertNotNull(output);
        assertEquals(created.id(), output.id());
        assertEquals("ABC123", output.code());
        assertEquals("Cupom de teste", output.description());
        assertEquals(BigDecimal.valueOf(0.8), output.discountValue());
        assertEquals("ACTIVE", output.status());
        assertTrue(output.published());
        assertFalse(output.deleted());
    }

    @Test
    void executeShouldReturnExpiredStatus() {
        CouponJpaEntity expired = repository.save(new CouponJpaEntity(
                null,
                "EXP123",
                "Cupom expirado",
                BigDecimal.valueOf(0.8),
                LocalDateTime.now().minusHours(1),
                true,
                false,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(2)
        ));

        var output = service.execute(new GetCouponCommand(expired.getId()));

        assertEquals("EXPIRED", output.status());
        assertFalse(output.deleted());
    }

    @Test
    void executeShouldReturnDeletedStatus() {
        CouponJpaEntity deleted = repository.save(new CouponJpaEntity(
                null,
                "DEL123",
                "Cupom deletado",
                BigDecimal.valueOf(0.8),
                LocalDateTime.now().plusDays(1),
                true,
                true,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusHours(3)
        ));

        var output = service.execute(new GetCouponCommand(deleted.getId()));

        assertEquals("DELETED", output.status());
        assertTrue(output.deleted());
    }

    @Test
    void executeShouldThrowWhenCouponDoesNotExist() {
        assertThrows(NotFoundException.class, () -> service.execute(new GetCouponCommand(99L)));
    }
}
