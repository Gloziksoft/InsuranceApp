package com.insurance.app.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = InsuranceDatesValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidInsuranceDates {

    String message() default "Dátum konca musí byť po dátume začiatku";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}