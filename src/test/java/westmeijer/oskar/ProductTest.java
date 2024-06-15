package westmeijer.oskar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import westmeijer.oskar.model.Product;

public class ProductTest {

  private Validator validator;

  @BeforeEach
  public void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  public void validProduct() {
    Product p = new Product(12, "Effective Java");
    Set<ConstraintViolation<Product>> violations = validator.validate(p);

    assertTrue(violations.isEmpty());
  }

  @Test
  public void idIsNull() {
    Product p = new Product(null, "Effective Java");

    Set<ConstraintViolation<Product>> violations = validator.validate(p);

    assertEquals(1, violations.size());
  }

  @Test
  public void nameIsNull() {
    Product p = new Product(123, null);
    Set<ConstraintViolation<Product>> violations = validator.validate(p);
    List<String> messages = violations.stream()
        .map(ConstraintViolation::getMessage)
        .toList();

    assertEquals(2, violations.size());
    assertTrue(messages.contains("Name cannot be null."));
    assertTrue(messages.contains("Name cannot be empty."));
  }


}
