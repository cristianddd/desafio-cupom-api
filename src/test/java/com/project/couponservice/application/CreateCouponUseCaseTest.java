package com.project.couponservice.application;

import com.project.couponservice.application.create.CreateCouponCommand;
import com.project.couponservice.application.create.CreateCouponOutput;
import com.project.couponservice.application.create.CreateCouponService;
import com.project.couponservice.domain.DomainException;
import com.project.couponservice.infra.repository.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CreateCouponUseCaseTest {

    @Autowired
    private CreateCouponService service;

    @Autowired
    private CouponRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void executeShouldPersistCouponAndReturnOutput() {
        CreateCouponCommand command = new CreateCouponCommand(
                "AB#CD12",
                "Test description",
                BigDecimal.ONE,
                LocalDateTime.now().plusDays(1),
                true
        );

        CreateCouponOutput output = service.execute(command);

        assertNotNull(output);
        assertNotNull(output.id());
        assertEquals("ABCD12", output.code());
        assertEquals(1, repository.count());

        var persisted = repository.findById(output.id()).orElseThrow();
        assertEquals("ABCD12", persisted.getCode());
        assertEquals("Test description", persisted.getDescription());
        assertTrue(persisted.isPublished());
        assertFalse(persisted.isDeleted());
    }

    @Test
    void executeShouldThrowWhenCouponCodeAlreadyExists() {
        CreateCouponCommand firstCommand = new CreateCouponCommand(
                "AB#CD12",
                "Test description",
                BigDecimal.ONE,
                LocalDateTime.now().plusDays(1),
                true
        );
        service.execute(firstCommand);

        CreateCouponCommand duplicatedCode = new CreateCouponCommand(
                "ABCD12",
                "Another description",
                BigDecimal.valueOf(2),
                LocalDateTime.now().plusDays(2),
                false
        );

        assertThrows(DomainException.class, () -> service.execute(duplicatedCode));
        assertEquals(1, repository.count());
    }
}
