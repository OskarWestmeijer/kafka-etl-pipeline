package westmeijer.oskar.steps.price;

import static westmeijer.oskar.steps.CloudEventMetadata.ceEventTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import westmeijer.oskar.service.model.Product;
import westmeijer.oskar.steps.StepProducer;
import westmeijer.oskar.steps.Steps;

@Slf4j
@Component
public class PriceStepProducer implements StepProducer {

  private final KafkaTemplate<String, CloudEvent> binaryCloudEventsKafkaTemplate;
  private final ObjectMapper objectMapper;

  public PriceStepProducer(
      @Qualifier(value = "binaryCloudEventsKafkaTemplate")
      KafkaTemplate<String, CloudEvent> binaryCloudEventsKafkaTemplate,
      ObjectMapper objectMapper) {
    this.binaryCloudEventsKafkaTemplate = binaryCloudEventsKafkaTemplate;
    this.objectMapper = objectMapper;
  }

  @Override
  public void produce(Product product) {
    Objects.requireNonNull(product);
    log.info("Producing to topic: {}, message: {}", getOutgoingTopic(), product);
    String productJson;
    try {
      productJson = objectMapper.writeValueAsString(product);
    } catch (JsonProcessingException exception) {
      log.error("Error on serialization.");
      throw new RuntimeException(exception);
    }

    var productCE = ceEventTemplate
        .withId(UUID.randomUUID().toString())
        .withTime(OffsetDateTime.ofInstant(Instant.now(), ZoneId.of("Europe/Berlin")))
        .withData(productJson.getBytes(StandardCharsets.UTF_8))
        .build();

    binaryCloudEventsKafkaTemplate.send(getOutgoingTopic(), String.valueOf(product.id()), productCE);
  }

  @Override
  public String getOutgoingTopic() {
    return Steps.PRICE_ASSIGNMENT.outputTopic;
  }

}
