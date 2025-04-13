package westmeijer.oskar.steps.stock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import westmeijer.oskar.service.model.Product;
import westmeijer.oskar.steps.StepProcessor;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockStepProcessor implements StepProcessor {

  @Override
  public void process(Product product) {
    var processedProduct = product.toBuilder()
        .stock(17)
        .build();
    log.info("Processed product. product: {}", processedProduct);
    log.info("Reached END of processing chain.");
  }

}
