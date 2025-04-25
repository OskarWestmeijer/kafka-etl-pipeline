package westmeijer.oskar.controller;

import static org.assertj.core.api.BDDAssertions.then;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static westmeijer.oskar.config.kafka.MetricsDefinition.CATEGORY_ASSIGNED;
import static westmeijer.oskar.config.kafka.MetricsDefinition.CATEGORY_ERROR;
import static westmeijer.oskar.config.kafka.MetricsDefinition.PRICE_ASSIGNED;
import static westmeijer.oskar.config.kafka.MetricsDefinition.PRICE_ERROR;
import static westmeijer.oskar.config.kafka.MetricsDefinition.PRODUCT_FINALIZED;
import static westmeijer.oskar.config.kafka.MetricsDefinition.PRODUCT_FINALIZED_ERROR;
import static westmeijer.oskar.config.kafka.MetricsDefinition.PRODUCT_RECEIVED;
import static westmeijer.oskar.config.kafka.MetricsDefinition.PRODUCT_RECEIVED_ERROR;
import static westmeijer.oskar.config.kafka.MetricsDefinition.STOCK_ASSIGNED;
import static westmeijer.oskar.config.kafka.MetricsDefinition.STOCK_ERROR;

import io.micrometer.core.instrument.MeterRegistry;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import westmeijer.oskar.controller.model.ProductRequest;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class ProductsControllerIT {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private MeterRegistry meterRegistry;

  @BeforeEach
  public void init() {
    meterRegistry.clear();

    meterRegistry.counter(PRODUCT_RECEIVED).count();
    meterRegistry.counter(CATEGORY_ASSIGNED).count();
    meterRegistry.counter(PRICE_ASSIGNED).count();
    meterRegistry.counter(STOCK_ASSIGNED).count();
    meterRegistry.counter(PRODUCT_FINALIZED).count();

    meterRegistry.counter(PRODUCT_RECEIVED_ERROR).count();
    meterRegistry.counter(CATEGORY_ERROR).count();
    meterRegistry.counter(PRICE_ERROR).count();
    meterRegistry.counter(STOCK_ERROR).count();
    meterRegistry.counter(PRODUCT_FINALIZED_ERROR).count();
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
  void shouldRunPipeline() {
    var productRequest = new ProductRequest(1234, "Effective Java");

    mockMvc.perform(post("/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                   "id":%s,
                   "name":"%s"
                }""".formatted(productRequest.id(), productRequest.name())))
        .andExpect(status().isAccepted());

    await().atMost(20, TimeUnit.SECONDS).untilAsserted(() -> {
      then(meterRegistry.get(PRODUCT_RECEIVED).counter().count()).isEqualTo(1d);
      then(meterRegistry.get(CATEGORY_ASSIGNED).counter().count()).isEqualTo(1d);
      then(meterRegistry.get(PRICE_ASSIGNED).counter().count()).isEqualTo(1d);
      then(meterRegistry.get(STOCK_ASSIGNED).counter().count()).isEqualTo(1d);
      then(meterRegistry.get(PRODUCT_FINALIZED).counter().count()).isEqualTo(1d);

      then(meterRegistry.get(PRODUCT_RECEIVED_ERROR).counter().count()).isEqualTo(0d);
      then(meterRegistry.get(CATEGORY_ERROR).counter().count()).isEqualTo(0d);
      then(meterRegistry.get(PRICE_ERROR).counter().count()).isEqualTo(0d);
      then(meterRegistry.get(STOCK_ERROR).counter().count()).isEqualTo(0d);
      then(meterRegistry.get(PRODUCT_FINALIZED_ERROR).counter().count()).isEqualTo(0d);
    });

  }

}
