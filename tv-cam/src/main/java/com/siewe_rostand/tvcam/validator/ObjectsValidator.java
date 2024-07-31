package com.siewe_rostand.tvcam.validator;

import com.siewe_rostand.tvcam.exceptions.ObjectValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author rostand
 * @project tv-cam
 */

@Service
public class ObjectsValidator<T> {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    public void validate(T objectToValidate) {
        Set<ConstraintViolation<T>> violations =  validator.validate(objectToValidate);
        System.out.println("++++++++++++++++++++++++++++++++++++++++++");
        if (!violations.isEmpty()) {
            System.out.println("ObjectsValidator.validate========================================");
            Set<String> errorMsg = violations
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.toSet());
            System.out.println("objectToValidate ++++++++++++++= " + errorMsg);
            throw new ObjectValidationException(errorMsg, objectToValidate.getClass().getSimpleName());
        }
    }
}
