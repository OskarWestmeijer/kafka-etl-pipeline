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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import westmeijer.oskar.model.Product;

@Slf4j
@Component
public class ProductsCEBinaryProducer {

  private final String productsCEBinaryTopic;
  private final KafkaTemplate<String, CloudEvent> productCEBinaryKafkaTemplate;
  private final ObjectMapper objectMapper;

  public ProductsCEBinaryProducer(
      @Value(value = "${kafka.servers.products.consumers.products-ce-binary.topic-name}") String productsCEBinaryTopic,
      @Qualifier(value = "productsCEStructuredKafkaTemplate") KafkaTemplate<String, CloudEvent> productCEBinaryKafkaTemplate,
      ObjectMapper objectMapper) {
    this.productsCEBinaryTopic = productsCEBinaryTopic;
    this.productCEBinaryKafkaTemplate = productCEBinaryKafkaTemplate;
    this.objectMapper = objectMapper;
  }

  private final CloudEventBuilder ceEventTemplate = CloudEventBuilder.v1()
      .withSource(URI.create("https://oskar-westmeijer.com"))
      .withType("products-ce-binary")
      .withDataContentType("application/cloudevents+json");

  public void sendMessage(Product product) {
    Objects.requireNonNull(product);
    log.info("Producing to topic: {}, message: {}", productsCEBinaryTopic, product);
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

    productCEBinaryKafkaTemplate.send(productsCEBinaryTopic, String.valueOf(product.id()), productCE);
  }

}
