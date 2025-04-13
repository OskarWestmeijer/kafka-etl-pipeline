package westmeijer.oskar.steps;

import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import westmeijer.oskar.service.model.Product;

@Slf4j
@Component
@RequiredArgsConstructor
public class StepValidator {

  private final Validator stepValidator;

  public void validate(Product product) {
    var validationErrors = stepValidator.validate(product);
    if (!validationErrors.isEmpty()) {
      log.error("Message had validation errors! errors: {}", validationErrors);
      throw new IllegalArgumentException(validationErrors.toString());
    }
  }

}
