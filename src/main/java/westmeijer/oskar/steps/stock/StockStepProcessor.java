package westmeijer.oskar.steps.stock;

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
class StockStepProcessor implements StepProcessor {

  private final MeterRegistry meterRegistry;
  private final StockStepHandOff stockStepHandOff;
  private final StockHttpClient stockHttpClient;

  @Override
  public void process(Product product) {
    var stockResponse = stockHttpClient.getStocks(product.id());
    var processedProduct = product.toBuilder()
        .stock(stockResponse.stock())
        .build();
    log.info("Processed product. product: {}", processedProduct);
    stockStepHandOff.produce(processedProduct);
    meterRegistry.counter(MetricsDefinition.STOCK_ASSIGNED).increment();
  }

}
