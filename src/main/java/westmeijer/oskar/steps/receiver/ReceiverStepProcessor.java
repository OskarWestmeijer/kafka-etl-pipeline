package westmeijer.oskar.steps.receiver;

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
public class ReceiverStepProcessor implements StepProcessor {

  private final MeterRegistry meterRegistry;
  private final ReceiverStepHandOff receiverStepHandOff;

  @Override
  public void process(Product product) {
    receiverStepHandOff.produce(product);
    meterRegistry.counter(MetricsDefinition.PRODUCT_RECEIVED).increment();
  }
}
