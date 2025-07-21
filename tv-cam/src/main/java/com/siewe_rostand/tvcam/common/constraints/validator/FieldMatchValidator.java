package com.siewe_rostand.tvcam.common.constraints.validator;

import com.siewe_rostand.tvcam.common.constraints.FieldMatch;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.beanutils.BeanUtils;

/**
 * @author rostand
 * @project tvcam
 */
public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {

  private String firstFieldName;
  private String secondFieldName;

  @Override
  public void initialize(final FieldMatch constraintAnnotation) {
    firstFieldName = constraintAnnotation.first();
    secondFieldName = constraintAnnotation.second();
  }

  @Override
  public boolean isValid(final Object value, final ConstraintValidatorContext context) {
    try {
      final Object firstObj = BeanUtils.getProperty(value, firstFieldName);
      final Object secondObj = BeanUtils.getProperty(value, secondFieldName);

      return firstObj == null && secondObj == null
          || firstObj != null && firstObj.equals(secondObj);
    } catch (final Exception ignore) {
    }

    return true;
  }
}
