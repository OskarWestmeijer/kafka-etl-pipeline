package westmeijer.oskar.controller;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.SneakyThrows;
import org.awaitility.Durations;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import westmeijer.oskar.consumer.ProductsCEStructuredConsumer;
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
  private ProductsCEStructuredConsumer productsCEStructuredConsumer;

  @BeforeEach
  @AfterEach
  public void init() {
    meterRegistry.clear();
    productsConsumer.clearLastMessage();
    productsCEStructuredConsumer.clearLastMessage();
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
      assertEquals(expectedProduct, productsConsumer.getLatestMsg());
      assertEquals(1d, meterRegistry.get("products.consumed").counter().count());
    });

    await().atMost(Durations.TEN_SECONDS).untilAsserted(() -> {
      assertEquals(expectedProduct, productsCEStructuredConsumer.getLatestMsg());
      assertEquals(1d, meterRegistry.get("products-ce-structured.consumed").counter().count());
    });
  }

}
