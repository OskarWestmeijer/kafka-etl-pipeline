package westmeijer.oskar.producer;

import static org.assertj.core.api.BDDAssertions.then;
import static org.awaitility.Awaitility.await;
import static westmeijer.oskar.config.kafka.MetricsDefinition.PRODUCTS_CE_STRUCTURED_CONSUMED;
import static westmeijer.oskar.config.kafka.MetricsDefinition.PRODUCTS_CE_STRUCTURED_ERROR;
import static westmeijer.oskar.config.kafka.MetricsDefinition.PRODUCTS_CONSUMED;
import static westmeijer.oskar.config.kafka.MetricsDefinition.PRODUCTS_ERROR;

import io.micrometer.core.instrument.MeterRegistry;
import java.util.concurrent.TimeUnit;
import org.awaitility.Durations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import westmeijer.oskar.config.kafka.MetricsDefinition;
import westmeijer.oskar.consumer.ProductsConsumer;
import westmeijer.oskar.model.Product;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class ProductsIT {

  @Autowired
  private ProductsProducer producer;

  @Autowired
  private ProductsConsumer consumer;

  @Autowired
  private MeterRegistry meterRegistry;

  @BeforeEach
  public void init() {
    meterRegistry.clear();
    meterRegistry.counter(PRODUCTS_CONSUMED).count();
    meterRegistry.counter(PRODUCTS_ERROR).count();
    meterRegistry.counter(PRODUCTS_CE_STRUCTURED_CONSUMED).count();
    meterRegistry.counter(PRODUCTS_CE_STRUCTURED_ERROR).count();
    consumer.clearLastMessage();
  }

  @Test
  void shouldProduceAndConsumeMessage() {
    Product p = new Product(1234, "System Design Interview");

    producer.sendMessage(p);

    await().atMost(Durations.TEN_SECONDS).untilAsserted(() -> {
      then(p).isEqualTo(consumer.getLatestMsg());
      then(meterRegistry.get(MetricsDefinition.PRODUCTS_CONSUMED).counter().count()).isEqualTo(1d);
      then(meterRegistry.get(MetricsDefinition.PRODUCTS_ERROR).counter().count()).isEqualTo(0d);
    });
  }

  @Test
  void shouldProduceAndDetectInvalidMessage() {
    Product p = new Product(-999, "System Design Interview");

    producer.sendMessage(p);

    await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
      then(meterRegistry.get(MetricsDefinition.PRODUCTS_ERROR).counter().count()).isEqualTo(1d);
      then(meterRegistry.get(MetricsDefinition.PRODUCTS_CONSUMED).counter().count()).isEqualTo(0d);
      then(consumer.getLatestMsg()).isNull();
    });
  }

}
