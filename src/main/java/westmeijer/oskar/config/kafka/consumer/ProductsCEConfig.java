package westmeijer.oskar.config.kafka.consumer;

import io.cloudevents.CloudEvent;
import io.cloudevents.kafka.CloudEventDeserializer;
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
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.util.backoff.FixedBackOff;
import westmeijer.oskar.config.kafka.MetricsDefinition;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class ProductsCEConfig {

  @Value(value = "${kafka.servers.products.bootstrap-server}")
  private String bootstrapAddress;

  @Value(value = "${kafka.servers.products.consumers.products-ce-structured.group-id}")
  private String groupId;

  private final MeterRegistry meterRegistry;

  private ConsumerFactory<String, CloudEvent> productsCEConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
    props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
    props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, CloudEventDeserializer.class);

    return new DefaultKafkaConsumerFactory<>(
        props,
        new StringDeserializer(),
        new CloudEventDeserializer()
    );
  }

  private DefaultErrorHandler defaultErrorHandler(String metric) {
    return new DefaultErrorHandler((record, exception) -> {
      log.error("Record consumption failed. {}", record, exception);
      meterRegistry.counter(metric).increment();
    }, new FixedBackOff(50L, 1L));
  }

  @Bean
  ConcurrentKafkaListenerContainerFactory<String, CloudEvent> productsCEStructuredContainerFactory() {
    var factory = new ConcurrentKafkaListenerContainerFactory<String, CloudEvent>();
    factory.setConsumerFactory(productsCEConsumerFactory());
    factory.setCommonErrorHandler(defaultErrorHandler(MetricsDefinition.PRODUCTS_CE_STRUCTURED_ERROR));
    return factory;
  }

  @Bean
  ConcurrentKafkaListenerContainerFactory<String, CloudEvent> productsCEBinaryContainerFactory() {
    var factory = new ConcurrentKafkaListenerContainerFactory<String, CloudEvent>();
    factory.setConsumerFactory(productsCEConsumerFactory());
    factory.setCommonErrorHandler(defaultErrorHandler(MetricsDefinition.PRODUCTS_CE_BINARY_ERROR));
    return factory;
  }

}
