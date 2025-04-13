package westmeijer.oskar.steps.category;

import io.cloudevents.CloudEvent;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import westmeijer.oskar.config.kafka.MetricsDefinition;
import westmeijer.oskar.steps.StepConsumer;
import westmeijer.oskar.steps.StepMapper;
import westmeijer.oskar.steps.StepValidator;
import westmeijer.oskar.steps.Steps.Topics;

@Slf4j
@RequiredArgsConstructor
@Component
public class CategoryStepConsumer implements StepConsumer {

  private final StepValidator stepValidator;
  private final StepMapper stepMapper;
  private final MeterRegistry meterRegistry;
  private final CategoryStepProcessor categoryStepProcessor;

  @KafkaListener(topics = Topics.CATEGORY,
      containerFactory = "binaryCloudEventContainerFactory")
  @Override
  public void consume(ConsumerRecord<String, CloudEvent> message) {
    var cloudEvent = message.value();
    log.info("Received message: {}", cloudEvent);
    var product = stepMapper.map(cloudEvent);
    stepValidator.validate(product);
    categoryStepProcessor.process(product);
    meterRegistry.counter(MetricsDefinition.PRICE_ASSIGNED).increment();
  }

}
