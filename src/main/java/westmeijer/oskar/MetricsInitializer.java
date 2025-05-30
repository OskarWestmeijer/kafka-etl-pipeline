package westmeijer.oskar;

import static westmeijer.oskar.config.kafka.MetricsDefinition.CATEGORY_ASSIGNED;
import static westmeijer.oskar.config.kafka.MetricsDefinition.CATEGORY_ERROR;
import static westmeijer.oskar.config.kafka.MetricsDefinition.PRICE_ASSIGNED;
import static westmeijer.oskar.config.kafka.MetricsDefinition.PRICE_ERROR;
import static westmeijer.oskar.config.kafka.MetricsDefinition.PRODUCT_FINALIZED;
import static westmeijer.oskar.config.kafka.MetricsDefinition.PRODUCT_FINALIZED_ERROR;
import static westmeijer.oskar.config.kafka.MetricsDefinition.PRODUCT_RECEIVED;
import static westmeijer.oskar.config.kafka.MetricsDefinition.PRODUCT_RECEIVED_ERROR;
import static westmeijer.oskar.config.kafka.MetricsDefinition.STOCK_ASSIGNED;
import static westmeijer.oskar.config.kafka.MetricsDefinition.STOCK_ERROR;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MetricsInitializer {

  private final MeterRegistry meterRegistry;

  @PostConstruct
  public void initCounters() {
    meterRegistry.counter(PRODUCT_RECEIVED);
    meterRegistry.counter(CATEGORY_ASSIGNED);
    meterRegistry.counter(PRICE_ASSIGNED);
    meterRegistry.counter(STOCK_ASSIGNED);
    meterRegistry.counter(PRODUCT_FINALIZED);

    meterRegistry.counter(PRODUCT_RECEIVED_ERROR).count();
    meterRegistry.counter(CATEGORY_ERROR).count();
    meterRegistry.counter(PRICE_ERROR).count();
    meterRegistry.counter(STOCK_ERROR).count();
    meterRegistry.counter(PRODUCT_FINALIZED_ERROR).count();
  }
}

