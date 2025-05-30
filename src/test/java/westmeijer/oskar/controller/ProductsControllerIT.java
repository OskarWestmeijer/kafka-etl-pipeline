package westmeijer.oskar.controller;

import static org.assertj.core.api.BDDAssertions.then;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import westmeijer.oskar.IntegrationTest;
import westmeijer.oskar.MetricsInitializer;
import westmeijer.oskar.controller.model.ProductRequest;

@IntegrationTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductsControllerIT {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private MeterRegistry meterRegistry;

  @Autowired
  private MetricsInitializer metricsInitializer;

  private static WireMockServer wireMockServer;

  private final Integer testProductId = 1234;
  private final String firstRunName = "Effective Java";
  private final String secondRunName = "Effective Java 2nd edition";

  @BeforeAll
  public void init() {
    wireMockServer = new WireMockServer(
        WireMockConfiguration.options()
            .port(9000)
            .usingFilesUnderClasspath("wiremock")
    );
    wireMockServer.start();
    meterRegistry.clear();
    metricsInitializer.initCounters();
  }

  @AfterAll
  void tearDown() {
    wireMockServer.stop();
  }

  @Test
  @Order(1)
  @SneakyThrows
  void shouldPingPong() {
    mockMvc.perform(get("/ping"))
        .andExpect(status().isOk())
        .andExpect(content().contentType("text/plain;charset=UTF-8"))
        .andExpect(content().string("pong"));
  }

  @Test
  @Order(2)
  @SneakyThrows
  void shouldNotHaveProduct() {
    mockMvc.perform(get("/products/%s".formatted(testProductId)))
        .andExpect(status().isNotFound());
  }

  @Test
  @Order(3)
  @SneakyThrows
  void shouldRunPipelineFirstTime() {
    var productRequest = new ProductRequest(testProductId, firstRunName);

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

  @Test
  @Order(4)
  @SneakyThrows
  void shouldHaveFinalizedProductFirstTime() {
    mockMvc.perform(get("/products/%s".formatted(testProductId)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(testProductId))
        .andExpect(jsonPath("$.name").value(firstRunName))
        .andExpect(jsonPath("$.category").value("Comic"))
        .andExpect(jsonPath("$.price").value("10.99"))
        .andExpect(jsonPath("$.stock").value("17"))
        .andExpect(jsonPath("$.createdAt").isNotEmpty())
        .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty())
        .andExpect(jsonPath("$.lastFinalizedAt").isNotEmpty());
  }

  @Test
  @Order(5)
  @SneakyThrows
  void shouldRunPipelineSecondTime() {
    var productRequest = new ProductRequest(testProductId, secondRunName);

    mockMvc.perform(post("/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                   "id":%s,
                   "name":"%s"
                }""".formatted(productRequest.id(), productRequest.name())))
        .andExpect(status().isAccepted());

    await().atMost(20, TimeUnit.SECONDS).untilAsserted(() -> {
      then(meterRegistry.get(PRODUCT_RECEIVED).counter().count()).isEqualTo(2d);
      then(meterRegistry.get(CATEGORY_ASSIGNED).counter().count()).isEqualTo(2d);
      then(meterRegistry.get(PRICE_ASSIGNED).counter().count()).isEqualTo(2d);
      then(meterRegistry.get(STOCK_ASSIGNED).counter().count()).isEqualTo(2d);
      then(meterRegistry.get(PRODUCT_FINALIZED).counter().count()).isEqualTo(2d);

      then(meterRegistry.get(PRODUCT_RECEIVED_ERROR).counter().count()).isEqualTo(0d);
      then(meterRegistry.get(CATEGORY_ERROR).counter().count()).isEqualTo(0d);
      then(meterRegistry.get(PRICE_ERROR).counter().count()).isEqualTo(0d);
      then(meterRegistry.get(STOCK_ERROR).counter().count()).isEqualTo(0d);
      then(meterRegistry.get(PRODUCT_FINALIZED_ERROR).counter().count()).isEqualTo(0d);
    });

  }

  @Test
  @Order(6)
  @SneakyThrows
  void shouldHaveFinalizedProductSecondTime() {
    mockMvc.perform(get("/products/%s".formatted(testProductId)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(testProductId))
        .andExpect(jsonPath("$.name").value(secondRunName))
        .andExpect(jsonPath("$.category").value("Comic"))
        .andExpect(jsonPath("$.price").value("10.99"))
        .andExpect(jsonPath("$.stock").value("17"))
        .andExpect(jsonPath("$.createdAt").isNotEmpty())
        .andExpect(jsonPath("$.lastModifiedAt").isNotEmpty())
        .andExpect(jsonPath("$.lastFinalizedAt").isNotEmpty());
  }

}
