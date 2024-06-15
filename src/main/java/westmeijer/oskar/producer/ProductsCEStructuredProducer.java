package westmeijer.oskar.producer;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import java.net.URI;
import java.util.Objects;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;
import westmeijer.oskar.model.Product;

@Slf4j
@Component
public class ProductsCEStructuredProducer {

  @Value(value = "${kafka.servers.products.consumers.products-ce-structured.topic-name}")
  private String productsCEStructuredTopic;

  @Autowired
  @Qualifier(value = "productsCEStructuredKafkaTemplate")
  private KafkaTemplate<String, CloudEvent> productCEStructuredKafkaTemplate;

  private final CloudEventBuilder ceEventTemplate = CloudEventBuilder.v1()
      .withSource(URI.create("https://oskar-westmeijer.com"))
      .withType("products-ce-structured");

  public void sendMessage(Product product) {
    Objects.requireNonNull(product);
    log.info("Producing to topic: {}, message: {}", productsCEStructuredTopic, product);

    var productCE = ceEventTemplate
        .withId(UUID.randomUUID().toString())
        .withData("application/cloudevents+json", SerializationUtils.serialize(product))
        .build();

    productCEStructuredKafkaTemplate.send(productsCEStructuredTopic, String.valueOf(product.id()), productCE);
  }

}
