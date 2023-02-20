package westmeijer.oskar;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProductsConsumer {

    private Product latestMsg;

    private final MeterRegistry meterRegistry;


    @KafkaListener(topics = "${products-consumers.topic-name}", containerFactory = "kafkaListenerContainerFactory")
    public void listenToProducts(ConsumerRecord<String, Product> message) {
        log.info("Consuming from products topic, message: {}", message);
        latestMsg = message.value();
        meterRegistry.counter("products.consumed").increment();
    }

    public Product getLatestMsg() {
        return latestMsg;
    }
}
