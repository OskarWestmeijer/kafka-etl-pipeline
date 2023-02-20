package westmeijer.oskar;

import io.micrometer.core.instrument.MeterRegistry;
import org.awaitility.Durations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;


import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class EmbeddedKafkaIntegrationTest {
    @Autowired
    private ProductsProducer producer;

    @Autowired
    private ProductsConsumer consumer;

    @Autowired
    private MeterRegistry meterRegistry;

    @BeforeEach
    public void init() {
        meterRegistry.clear();
    }


    @Test
    public void testKafka() {
        Product p = new Product(1234, "System Design Interview");

        producer.sendMessage(p);

        await().atMost(Durations.TEN_SECONDS).untilAsserted(() -> {
            assertEquals(p, consumer.getLatestMsg());
            assertEquals(1d, meterRegistry.get("products.consumed").counter().count());
        });
    }

}
