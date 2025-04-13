package westmeijer.oskar.steps.stock;

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

@Slf4j
@RequiredArgsConstructor
@Component
public class StockStepConsumer implements StepConsumer {

  private final Validator validator;

  private final ObjectMapper objectMapper;

  private final MeterRegistry meterRegistry;

  private final StockStepProcessor stockStepProcessor;

  @KafkaListener(topics = "${kafka.servers.products.steps.stock-assignment.topic-name}",
      containerFactory = "binaryCloudEventContainerFactory")
  public void consume(ConsumerRecord<String, CloudEvent> message) {
    log.info("Received message from products topic. key: {}, value: {}, message: {}", message.key(), message.value(), message);
    var cloudEvent = message.value();

    PojoCloudEventData<Product> deserializedData = CloudEventUtils
        .mapData(cloudEvent, PojoCloudEventDataMapper.from(objectMapper, Product.class));
    requireNonNull(deserializedData, "message data is required");
    var product = deserializedData.getValue();

    var validationErrors = validator.validate(product);
    if (!validationErrors.isEmpty()) {
      log.error("Message had validation errors! errors: {}", validationErrors);
      throw new IllegalArgumentException(validationErrors.toString());
    }

    stockStepProcessor.process(product);
    meterRegistry.counter(MetricsDefinition.PRODUCTS_CONSUMED).increment();
  }

}
