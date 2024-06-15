package westmeijer.oskar.consumer;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.validation.Validator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import westmeijer.oskar.model.Product;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProductsConsumer {

  @Getter
  private Product latestMsg;

  private final Validator validator;

  private final MeterRegistry meterRegistry;

  @KafkaListener(topics = "${kafka.servers.products.consumers.products.topic-name}",
      containerFactory = "productsContainerFactory")
  public void listenToProducts(ConsumerRecord<String, Product> message) {
    log.info("Received message from products topic. key: {}, value: {}, message: {}", message.key(), message.value(), message);
    var validationErrors = validator.validate(message.value());
    if (!validationErrors.isEmpty()) {
      log.error("Message had validation errors! errors: {}", validationErrors);
      throw new IllegalArgumentException(validationErrors.toString());
    }

    latestMsg = message.value();
    meterRegistry.counter("products.consumed").increment();
  }

}
