package westmeijer.oskar.producer;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import westmeijer.oskar.model.Product;

@Slf4j
@Component
public class ProductsCEStructuredProducer {

  @Value(value = "${kafka.servers.products.consumers.products-ce-structured.topic-name}")
  private String productsCEStructuredTopic;

  @Autowired
  @Qualifier(value = "productsCEStructuredKafkaTemplate")
  private KafkaTemplate<String, Product> productCEStructuredKafkaTemplate;

  public void sendMessage(Product product) {
    Objects.requireNonNull(product);
    log.info("Producing to topic: {}, message: {}", productsCEStructuredTopic, product);
    productCEStructuredKafkaTemplate.send(productsCEStructuredTopic, String.valueOf(product.id()), product);
  }

}
