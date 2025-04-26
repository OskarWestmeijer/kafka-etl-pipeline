package westmeijer.oskar.steps.stock;

import io.cloudevents.CloudEvent;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import westmeijer.oskar.service.model.Product;
import westmeijer.oskar.steps.StepHandOff;
import westmeijer.oskar.steps.StepMapper;
import westmeijer.oskar.steps.Steps;

@Slf4j
@Component
@RequiredArgsConstructor
class StockStepHandOff implements StepHandOff {

  private final KafkaTemplate<String, CloudEvent> binaryCloudEventsKafkaTemplate;
  private final StepMapper stepMapper;

  @Override
  public void produce(Product product) {
    Objects.requireNonNull(product);
    log.info("Producing to topic: {}, message: {}", getOutgoingTopic(), product);
    binaryCloudEventsKafkaTemplate.send(getOutgoingTopic(), String.valueOf(product.id()), stepMapper.map(product));
  }

  @Override
  public String getOutgoingTopic() {
    return Steps.STOCK_ASSIGNMENT.outputTopic;
  }

}
