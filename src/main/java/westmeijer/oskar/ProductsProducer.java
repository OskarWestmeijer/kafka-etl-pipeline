package westmeijer.oskar;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class ProductsProducer {

    @Value(value = "${products-consumers.topic-name}")
    private String productsTopic;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String message) {
        Objects.requireNonNull(message);
        log.info("Sending message: {} , to topic: {}", productsTopic, message);
        kafkaTemplate.send(productsTopic, message);
    }

}
