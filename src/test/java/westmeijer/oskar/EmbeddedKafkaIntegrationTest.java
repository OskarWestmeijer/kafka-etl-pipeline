package westmeijer.oskar;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class EmbeddedKafkaIntegrationTest {

    @Autowired
    private ProductsConsumer consumer;

    @Autowired
    private ProductsProducer producer;

    @Value("${products-consumers.topic-name}")
    private String topic;

    @Test
    public void testKafka() throws Exception {
        Product p = new Product(1234, "System Design Interview");

        producer.sendMessage(p);
        new CountDownLatch(1).await(5, TimeUnit.SECONDS);
        Product consumed = consumer.getLatestMsg();

        assertEquals(p, consumed);
    }

}
