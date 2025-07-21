package com.siewe_rostand.tvcam.common.constraints.validator;

import static com.siewe_rostand.tvcam.utils.Helpers.logException;

import com.siewe_rostand.tvcam.common.constraints.IsUnique;
import com.siewe_rostand.tvcam.utils.Helpers;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author rostand
 * @project tvcam
 */
@Component
public class IsUniqueValidator implements ConstraintValidator<IsUnique, String> {

  private String propertyName;
  private String repositoryName;
  private UpdateAction action;

  private final ApplicationContext applicationContext;

  public IsUniqueValidator(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @Override
  public void initialize(IsUnique constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    Object result;
    String finalRepositoryName = repositoryName;

    try {
      Class<?> type = Class.forName(finalRepositoryName);
      Object instance = this.applicationContext.getBean(finalRepositoryName);

      final Object propertyObj = BeanUtils.getProperty(value, propertyName);
      final Object objId = BeanUtils.getProperty(value, "id");

      String finalPropertyName = Helpers.capitalize(propertyName);

      if (propertyObj == null) {
        return true;
      }

      result =
          type.getMethod("findBy" + finalPropertyName, String.class)
              .invoke(instance, propertyObj.toString());

      if (action == UpdateAction.INSERT) {
        return result == null;
      } else {
        Class<?> resultType = result.getClass();
        String resultId = resultType.getMethod("getId").invoke(result).toString();

        return resultId == objId;
      }
    } catch (ClassNotFoundException
        | IllegalAccessException
        | NoSuchMethodException
        | InvocationTargetException e) {

      logException(e);

      return false;
    }
  }

  public static enum UpdateAction {
    INSERT,
    UPDATE
  }
}
