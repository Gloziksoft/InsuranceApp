package com.insurance.app.validation;

import com.insurance.app.models.dto.InsuranceDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class InsuranceDatesValidator
        implements ConstraintValidator<ValidInsuranceDates, InsuranceDTO> {

    @Override
    public boolean isValid(
            InsuranceDTO dto,
            ConstraintValidatorContext context) {

        if (dto == null
                || dto.getStartDate() == null
                || dto.getEndDate() == null) {
            return true;
        }

        if (!dto.getEndDate().isAfter(dto.getStartDate())) {

            context.disableDefaultConstraintViolation();

            context.buildConstraintViolationWithTemplate(
                            context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("endDate")
                    .addConstraintViolation();

            return false;
        }

        return true;
    }
}