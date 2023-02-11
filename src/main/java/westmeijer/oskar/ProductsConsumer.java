package westmeijer.oskar;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProductsConsumer {

    @Value(value = "${products-consumers.topic-name}")
    private String productsTopic;

    // TODO: figure property injection for annotations out
    @KafkaListener(topics = "products", groupId = "1234")
    public void listenGroupFoo(String message) {
        log.info("Consuming from topic: {}, message: {}", productsTopic, message);
    }

}
