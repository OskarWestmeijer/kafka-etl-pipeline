package westmeijer.oskar.steps.stock;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import westmeijer.oskar.service.model.Product;
import westmeijer.oskar.steps.Steps;

@Slf4j
@Component
class StockStepProcessor extends StepProcessorAbstract {

  public StockStepProcessor(MeterRegistry meterRegistry, StockStepHandOff stockStepHandOff) {
    super(meterRegistry, stockStepHandOff, Steps.STOCK_ASSIGNMENT);
  }

  @Override
  public Product enrich(Product product) {
    return product.toBuilder()
        .stock(17)
        .build();
  }

}
