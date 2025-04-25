package westmeijer.oskar.steps.stock;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import westmeijer.oskar.service.model.Product;
import westmeijer.oskar.steps.StepHandOff;
import westmeijer.oskar.steps.Steps;

@Slf4j
@RequiredArgsConstructor
public abstract class StepProcessorAbstract {

  private final MeterRegistry meterRegistry;

  private final StepHandOff stepHandOff;

  private final Steps steps;

  abstract public Product enrich(Product product);

  public void process(Product product) {
    var processedProduct = enrich(product);
    stepHandOff.produce(processedProduct);
    log.info("Processed product. product: {}", processedProduct);
    meterRegistry.counter(steps.metricsDefinition).increment();
  }

}
