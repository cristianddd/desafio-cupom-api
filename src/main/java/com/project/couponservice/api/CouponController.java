package com.project.couponservice.api;

import com.project.couponservice.api.dto.CreateCouponRequest;
import com.project.couponservice.api.dto.CreateCouponResponse;
import com.project.couponservice.application.create.CreateCouponCommand;
import com.project.couponservice.application.create.CreateCouponOutput;
import com.project.couponservice.application.create.CreateCouponUseCase;
import com.project.couponservice.application.delete.DeleteCouponCommand;
import com.project.couponservice.application.delete.DeleteCouponUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CreateCouponUseCase createCouponUseCase;
    private final DeleteCouponUseCase deleteCouponUseCase;

    @PostMapping
    public ResponseEntity<CreateCouponResponse> create(@Valid @RequestBody CreateCouponRequest request) {
        CreateCouponCommand command = new CreateCouponCommand(request.getCode(), request.getDescription(),
                request.getDiscountValue(), request.getExpirationDate(), request.isPublished());

        CreateCouponOutput output = createCouponUseCase.execute(command);
        CreateCouponResponse response = new CreateCouponResponse(output.id(), output.code(), output.expirationDate());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteCouponUseCase.execute(new DeleteCouponCommand(id));
        return ResponseEntity.noContent().build();
    }
}