package westmeijer.oskar.controller;

import static org.assertj.core.api.BDDAssertions.then;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static westmeijer.oskar.config.kafka.MetricsDefinition.PRODUCTS_CE_STRUCTURED_CONSUMED;
import static westmeijer.oskar.config.kafka.MetricsDefinition.PRODUCTS_CE_STRUCTURED_ERROR;
import static westmeijer.oskar.config.kafka.MetricsDefinition.PRODUCTS_CONSUMED;
import static westmeijer.oskar.config.kafka.MetricsDefinition.PRODUCTS_ERROR;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.SneakyThrows;
import org.awaitility.Durations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import westmeijer.oskar.config.kafka.MetricsDefinition;
import westmeijer.oskar.steps.price.PriceStepConsumer;
import westmeijer.oskar.consumer.ProductsConsumer;
import westmeijer.oskar.model.Product;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class ProductsControllerIT {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private MeterRegistry meterRegistry;

  @Autowired
  private ProductsConsumer productsConsumer;

  @Autowired
  private PriceStepConsumer priceTopicConsumer;

  @BeforeEach
  public void init() {
    meterRegistry.clear();
    meterRegistry.counter(PRODUCTS_CONSUMED).count();
    meterRegistry.counter(PRODUCTS_ERROR).count();
    meterRegistry.counter(PRODUCTS_CE_STRUCTURED_CONSUMED).count();
    meterRegistry.counter(PRODUCTS_CE_STRUCTURED_ERROR).count();
    productsConsumer.clearLastMessage();
    priceTopicConsumer.clearLastMessage();
  }

  @Test
  @SneakyThrows
  void shouldPingPong() {
    mockMvc.perform(get("/ping"))
        .andExpect(status().isOk())
        .andExpect(content().contentType("text/plain;charset=UTF-8"))
        .andExpect(content().string("pong"));
  }

  @Test
  @SneakyThrows
  void shouldProduceAndConsumeMessages() {

    var expectedProduct = new Product(1234, "Effective Java");

    mockMvc.perform(post("/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                   "id":%s,
                   "name":"%s"
                }""".formatted(expectedProduct.id(), expectedProduct.name())))
        .andExpect(status().isCreated());

    await().atMost(Durations.TEN_SECONDS).untilAsserted(() -> {
      then(expectedProduct).isEqualTo(productsConsumer.getLatestMsg());
      then(expectedProduct).isEqualTo(priceTopicConsumer.getLatestMsg());
      then(meterRegistry.get(MetricsDefinition.PRODUCTS_CONSUMED).counter().count()).isEqualTo(1d);
      then(meterRegistry.get(MetricsDefinition.PRODUCTS_CE_STRUCTURED_CONSUMED).counter().count()).isEqualTo(1d);
    });

  }

}
