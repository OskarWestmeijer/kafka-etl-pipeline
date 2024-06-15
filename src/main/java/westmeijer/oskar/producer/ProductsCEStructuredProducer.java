package westmeijer.oskar.producer;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import westmeijer.oskar.model.Product;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductsCEStructuredProducer {

  @Value(value = "${kafka.servers.products.consumers.products-ce-structured.topic-name}")
  private String productsCEStructuredTopic;

  @Autowired
  @Qualifier(value = "productsCEStructuredKafkaTemplate")
  private final KafkaTemplate<String, CloudEvent> productCEStructuredKafkaTemplate;

  @Autowired
  private final ObjectMapper objectMapper;

  private final CloudEventBuilder ceEventTemplate = CloudEventBuilder.v1()
      .withSource(URI.create("https://oskar-westmeijer.com"))
      .withType("products-ce-structured")
      .withDataContentType("application/cloudevents+json");

  public void sendMessage(Product product) {
    Objects.requireNonNull(product);
    log.info("Producing to topic: {}, message: {}", productsCEStructuredTopic, product);
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

    productCEStructuredKafkaTemplate.send(productsCEStructuredTopic, String.valueOf(product.id()), productCE);
  }

}
