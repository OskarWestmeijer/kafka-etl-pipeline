package westmeijer.oskar.steps.finalizer;

import io.micrometer.core.instrument.MeterRegistry;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import westmeijer.oskar.config.kafka.MetricsDefinition;
import westmeijer.oskar.service.ProductsService;
import westmeijer.oskar.service.model.Product;
import westmeijer.oskar.steps.StepProcessor;

@Slf4j
@Component
@RequiredArgsConstructor
class FinalizerStepProcessor implements StepProcessor {

  private final MeterRegistry meterRegistry;
  private final ProductsService productsService;

  @Override
  public void process(Product product) {
    var finalizedProduct = product.toBuilder()
        .lastFinalizedAt(Instant.now())
        .build();
    var savedProduct = productsService.saveProduct(finalizedProduct);
    log.info("Processed product. product: {}", savedProduct);
    meterRegistry.counter(MetricsDefinition.PRODUCT_FINALIZED).increment();
  }
}
