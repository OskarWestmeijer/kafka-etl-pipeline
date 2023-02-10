package westmeijer.oskar;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProductQueueListener {
    /*
    @Bean
    public NewTopic topic() {
        return TopicBuilder.name("topic1")
                .partitions(10)
                .replicas(1)
                .build();
    }
    */

    @KafkaListener(id = "myId", topics = "products")
    public void listen(String msg) {
        log.info("Received message: {}", msg);

    }


}
