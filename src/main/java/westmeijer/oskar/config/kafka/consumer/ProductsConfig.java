package westmeijer.oskar.config.kafka.consumer;

import io.micrometer.core.instrument.MeterRegistry;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;
import westmeijer.oskar.config.kafka.MetricsDefinition;
import westmeijer.oskar.model.Product;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ProductsConfig {

  @Value(value = "${kafka.servers.products.bootstrap-server}")
  private String bootstrapAddress;

  @Value(value = "${kafka.servers.products.consumers.products.group-id}")
  private String groupId;

  private final MeterRegistry meterRegistry;

  @Bean
  ConsumerFactory<String, Product> productsConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    return new DefaultKafkaConsumerFactory<>(
        props,
        new StringDeserializer(),
        new JsonDeserializer<>(Product.class)
    );
  }

  @Bean
  ConcurrentKafkaListenerContainerFactory<String, Product> productsContainerFactory() {
    var factory = new ConcurrentKafkaListenerContainerFactory<String, Product>();
    factory.setConsumerFactory(productsConsumerFactory());
    factory.setCommonErrorHandler(new DefaultErrorHandler((record, exception) -> {
      log.error("Record consumption failed. {}", record, exception);
      meterRegistry.counter(MetricsDefinition.PRODUCTS_ERROR).increment();
    }, new FixedBackOff(50L, 1L)));
    return factory;
  }

}
