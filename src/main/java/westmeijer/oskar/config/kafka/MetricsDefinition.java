package westmeijer.oskar.config.kafka;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MetricsDefinition {

  public static final String PRODUCTS_CONSUMED = "products.consumed";
  public static final String PRODUCTS_ERROR = "products.error";

  public static final String PRODUCTS_CE_STRUCTURED_CONSUMED = "products-ce-structured.consumed";
  public static final String PRODUCTS_CE_STRUCTURED_ERROR = "products-ce-structured.error";

  public static final String PRODUCTS_CE_BINARY_CONSUMED = "products-ce-binary.consumed";
  public static final String PRODUCTS_CE_BINARY_ERROR = "products-ce-binary.error";

  private final MeterRegistry meterRegistry;

  @PostConstruct
  void init() {
    meterRegistry.counter(PRODUCTS_CONSUMED).count();
    meterRegistry.counter(PRODUCTS_ERROR).count();
    meterRegistry.counter(PRODUCTS_CE_STRUCTURED_CONSUMED).count();
    meterRegistry.counter(PRODUCTS_CE_STRUCTURED_ERROR).count();
    meterRegistry.counter(PRODUCTS_CE_BINARY_CONSUMED).count();
    meterRegistry.counter(PRODUCTS_CE_BINARY_ERROR).count();
  }

}
