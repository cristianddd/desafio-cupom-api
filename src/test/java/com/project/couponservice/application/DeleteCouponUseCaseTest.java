package com.project.couponservice.application;

import com.project.couponservice.application.create.CreateCouponCommand;
import com.project.couponservice.application.create.CreateCouponService;
import com.project.couponservice.application.delete.DeleteCouponCommand;
import com.project.couponservice.application.delete.DeleteCouponService;
import com.project.couponservice.domain.DomainException;
import com.project.couponservice.domain.NotFoundException;
import com.project.couponservice.infra.repository.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DeleteCouponUseCaseTest {

    @Autowired
    private DeleteCouponService service;

    @Autowired
    private CreateCouponService createCouponService;

    @Autowired
    private CouponRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void executeShouldSoftDeleteCoupon() {
        var created = createCouponService.execute(new CreateCouponCommand(
                "ABCD12",
                "Desc",
                BigDecimal.ONE,
                LocalDateTime.now().plusDays(1),
                false
        ));

        var output = service.execute(new DeleteCouponCommand(created.id()));

        assertNotNull(output);
        assertEquals(created.id(), output.id());

        var persisted = repository.findById(created.id()).orElseThrow();
        assertTrue(persisted.isDeleted());
    }

    @Test
    void executeShouldThrowWhenCouponNotFound() {
        assertThrows(NotFoundException.class, () -> service.execute(new DeleteCouponCommand(1L)));
    }

    @Test
    void executeShouldThrowWhenAlreadyDeleted() {
        var created = createCouponService.execute(new CreateCouponCommand(
                "EFGH34",
                "Desc",
                BigDecimal.ONE,
                LocalDateTime.now().plusDays(1),
                false
        ));
        service.execute(new DeleteCouponCommand(created.id()));

        assertThrows(DomainException.class, () -> service.execute(new DeleteCouponCommand(created.id())));
    }
}
