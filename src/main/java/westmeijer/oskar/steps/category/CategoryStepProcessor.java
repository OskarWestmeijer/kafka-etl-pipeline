package westmeijer.oskar.steps.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import westmeijer.oskar.service.model.Product;
import westmeijer.oskar.steps.StepProcessor;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryStepProcessor implements StepProcessor {

  private final CategoryStepProducer categoryStepProducer;

  @Override
  public void process(Product product) {
    var processedProduct = product.toBuilder()
        .category("Books")
        .build();
    log.info("Processed product. product: {}", processedProduct);
    categoryStepProducer.produce(processedProduct);
  }
}
