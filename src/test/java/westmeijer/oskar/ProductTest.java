package westmeijer.oskar;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Validations
 */
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
        NullPointerException thrown = assertThrows(NullPointerException.class, () -> {
            Product p = new Product(null, "Effective Java");
        });
    }

    @Test
    public void nameIsNull() {
        NullPointerException thrown = assertThrows(NullPointerException.class, () -> {
            Product p = new Product(12, null);
        });
    }


}
