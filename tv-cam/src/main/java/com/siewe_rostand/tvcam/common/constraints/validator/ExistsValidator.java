package com.siewe_rostand.tvcam.common.constraints.validator;

import com.siewe_rostand.tvcam.common.constraints.Exists;
import com.siewe_rostand.tvcam.common.utils.Helpers;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import static com.siewe_rostand.tvcam.common.utils.Helpers.logException;

/**
 * @author rostand
 * @project tvcam
 */
@Component
public class ExistsValidator implements ConstraintValidator<Exists, Object> {
  private String field;
  private String repository;

  private final ApplicationContext applicationContext;

  public ExistsValidator(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @Override
  public void initialize(Exists constraintAnnotation) {
    field = constraintAnnotation.property();
    repository = constraintAnnotation.repository();
  }

  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    Object result;
    String finalRepositoryName = repository;

    try {
      Class<?> type = Class.forName(finalRepositoryName);
      Object instance = this.applicationContext.getBean(finalRepositoryName);

      final Object propertyObj = BeanUtils.getProperty(value, field);
      String finalPropertyName = Helpers.capitalize(field);

      result = type.getMethod("findBy" + finalPropertyName, String.class)
          .invoke(instance, propertyObj.toString());
    } catch (ClassNotFoundException
        | IllegalAccessException
        | InvocationTargetException
        | NoSuchMethodException e) {
      logException(e);

      return false;
    }

    return result != null;
  }
}
