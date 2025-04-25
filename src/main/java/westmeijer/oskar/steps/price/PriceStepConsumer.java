package westmeijer.oskar.steps.price;

import io.cloudevents.CloudEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import westmeijer.oskar.steps.StepConsumer;
import westmeijer.oskar.steps.StepMapper;
import westmeijer.oskar.steps.StepValidator;
import westmeijer.oskar.steps.Steps.Topics;

@Slf4j
@Component
@RequiredArgsConstructor
class PriceStepConsumer implements StepConsumer {

  private final StepValidator stepValidator;
  private final StepMapper stepMapper;
  private final PriceStepProcessor priceStepProcessor;

  @KafkaListener(topics = Topics.CATEGORY_ASSIGNED,
      containerFactory = "binaryCloudEventContainerFactory")
  @Override
  public void consume(ConsumerRecord<String, CloudEvent> message) {
    var cloudEvent = message.value();
    log.info("Received message: {}", cloudEvent);
    var product = stepMapper.map(cloudEvent);
    stepValidator.validate(product);
    priceStepProcessor.process(product);
  }

}
