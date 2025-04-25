package westmeijer.oskar.steps.finalizer;

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
class FinalizerStepProcessor implements StepProcessor {

  private final MeterRegistry meterRegistry;

  @Override
  public void process(Product product) {
    log.info("Processed product. product: {}", product);
    meterRegistry.counter(MetricsDefinition.PRODUCT_FINALIZED).increment();
  }
}
