package westmeijer.oskar;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProductsConsumer {

    @Value(value = "${products-consumers.topic-name}")
    private String productsTopic;

    private Product latestMsg;

    private final Counter msgCounter;

    public ProductsConsumer(MeterRegistry meterRegistry) {
        this.msgCounter = Counter.builder("products.consumed").register(meterRegistry);
    }

    // TODO: figure property injection for annotations out
    @KafkaListener(topics = "products", containerFactory = "kafkaListenerContainerFactory")
    public void listenToProducts(Product message) {
        log.info("Consuming from topic: {}, message: {}", productsTopic, message);
        latestMsg = message;
        msgCounter.increment();
    }

    public Product getLatestMsg() {
        return latestMsg;
    }
}
