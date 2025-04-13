package westmeijer.oskar.steps.price;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.CloudEventUtils;
import io.cloudevents.core.data.PojoCloudEventData;
import io.cloudevents.jackson.PojoCloudEventDataMapper;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import westmeijer.oskar.config.kafka.MetricsDefinition;
import westmeijer.oskar.service.model.Product;
import westmeijer.oskar.steps.StepConsumer;
import westmeijer.oskar.steps.Steps.Topics;

@Slf4j
@RequiredArgsConstructor
@Component
public class PriceStepConsumer implements StepConsumer {

  private final Validator validator;

  private final ObjectMapper objectMapper;

  private final MeterRegistry meterRegistry;

  private final PriceStepProcessor priceStepProcessor;

  @KafkaListener(topics = Topics.PRICE,
      containerFactory = "binaryCloudEventContainerFactory")
  @Override
  public void consume(ConsumerRecord<String, CloudEvent> message) {
    var cloudEvent = message.value();
    log.info("Received message: {}", cloudEvent);

    PojoCloudEventData<Product> deserializedData = CloudEventUtils
        .mapData(cloudEvent, PojoCloudEventDataMapper.from(objectMapper, Product.class));
    requireNonNull(deserializedData, "message data is required");
    var product = deserializedData.getValue();

    var validationErrors = validator.validate(product);
    if (!validationErrors.isEmpty()) {
      log.error("Message had validation errors! errors: {}", validationErrors);
      throw new IllegalArgumentException(validationErrors.toString());
    }

    priceStepProcessor.process(product);
    meterRegistry.counter(MetricsDefinition.CATEGORY_ASSIGNED).increment();
  }

}
