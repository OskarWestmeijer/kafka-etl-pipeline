package westmeijer.oskar.steps.price;

import io.micrometer.core.instrument.MeterRegistry;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import westmeijer.oskar.config.kafka.MetricsDefinition;
import westmeijer.oskar.service.model.Product;
import westmeijer.oskar.steps.StepProcessor;

@Slf4j
@Component
@RequiredArgsConstructor
class PriceStepProcessor implements StepProcessor {

  private final PriceStepHandOff priceStepProducer;
  private final MeterRegistry meterRegistry;

  @Override
  public void process(Product product) {
    var processedProduct = product.toBuilder()
        .price(BigDecimal.valueOf(10.99d))
        .build();
    log.info("Processed product. product: {}", processedProduct);
    priceStepProducer.produce(processedProduct);
    meterRegistry.counter(MetricsDefinition.PRICE_ASSIGNED).increment();
  }

}
