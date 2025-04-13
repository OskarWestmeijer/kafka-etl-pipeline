package westmeijer.oskar.service;

import static westmeijer.oskar.steps.CloudEventMetadata.ceEventTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import westmeijer.oskar.service.model.Product;
import westmeijer.oskar.steps.Steps;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductsService {

  private final Steps initialStep = Steps.CATEGORY_ASSIGNMENT;
  private final KafkaTemplate<String, CloudEvent> binaryCloudEventsKafkaTemplate;
  private final ObjectMapper objectMapper;

  public void startProductProcessing(Product product) {
    Objects.requireNonNull(product);
    log.info("Producing to topic: {}, message: {}", initialStep.inputTopic, product);
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
    binaryCloudEventsKafkaTemplate.send(initialStep.inputTopic, String.valueOf(product.id()), productCE);
  }

}
