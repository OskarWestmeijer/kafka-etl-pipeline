package westmeijer.oskar;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.validation.Validator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProductsConsumer {

  @Getter
  private Product latestMsg;

  private final Validator validator;

  private final MeterRegistry meterRegistry;

  @KafkaListener(topics = "${products-consumers.topic-name}")
  public void listenToProducts(ConsumerRecord<String, Product> message) {
    log.info("Received message from products topic. key: {}, value: {}, message: {}", message.key(), message.value(), message);
    var validationErrors = validator.validate(message.value());
    if (!validationErrors.isEmpty()) {
      log.error("Message had validation errors! errors: {}", validationErrors);
    }

    latestMsg = message.value();
    meterRegistry.counter("products.consumed").increment();
  }

}
