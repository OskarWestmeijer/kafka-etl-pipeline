package westmeijer.oskar.steps.category;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import westmeijer.oskar.config.kafka.MetricsDefinition;
import westmeijer.oskar.service.model.Product;
import westmeijer.oskar.steps.StepProcessor;

@Slf4j
@Component
@RequiredArgsConstructor
class CategoryStepProcessor implements StepProcessor {

  private final MeterRegistry meterRegistry;
  private final CategoryStepHandOff categoryStepProducer;

  @Override
  public void process(Product product) {
    var processedProduct = product.toBuilder()
        .category("Books")
        .build();
    log.info("Processed product. product: {}", processedProduct);
    categoryStepProducer.produce(processedProduct);
    meterRegistry.counter(MetricsDefinition.CATEGORY_ASSIGNED).increment();
  }
}
