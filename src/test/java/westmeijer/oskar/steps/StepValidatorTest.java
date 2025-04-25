package westmeijer.oskar.steps;

import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.Test;
import westmeijer.oskar.service.model.Product;

class StepValidatorTest {

  @Test
  void shouldNotThrowWhenProductIsValid() {
    // given
    Validator validator = mock(Validator.class);
    Product product = mock(Product.class);
    given(validator.validate(product)).willReturn(Collections.emptySet());

    StepValidator stepValidator = new StepValidator(validator);

    // when / then
    stepValidator.validate(product);
  }

  @Test
  void shouldThrowWhenProductIsInvalid() {
    // given
    Validator validator = mock(Validator.class);
    Product product = mock(Product.class);

    ConstraintViolation<Product> violation = mock(ConstraintViolation.class);
    Set<ConstraintViolation<Product>> violations = Set.of(violation);
    given(validator.validate(product)).willReturn(violations);

    StepValidator stepValidator = new StepValidator(validator);

    // when / then
    thenThrownBy(() -> stepValidator.validate(product))
        .isInstanceOf(IllegalArgumentException.class);
  }

}