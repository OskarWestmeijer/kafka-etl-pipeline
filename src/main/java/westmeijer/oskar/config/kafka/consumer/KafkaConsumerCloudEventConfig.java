package westmeijer.oskar.config.kafka.consumer;

import io.cloudevents.CloudEvent;
import io.cloudevents.kafka.CloudEventDeserializer;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.errors.RecordDeserializationException;
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


@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaConsumerCloudEventConfig {

  @Value(value = "${kafka.servers.products.bootstrap-server}")
  private String bootstrapAddress;

  @Value(value = "${kafka.servers.products.group-id}")
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
    return new DefaultKafkaConsumerFactory<>(props);
  }

  @Bean
  ConcurrentKafkaListenerContainerFactory<String, CloudEvent> binaryCloudEventContainerFactory() {
    var factory = new ConcurrentKafkaListenerContainerFactory<String, CloudEvent>();
    factory.setConsumerFactory(productsCEConsumerFactory());
    factory.setCommonErrorHandler(kafkaErrorHandler());
    return factory;
  }

  @Bean
  DefaultErrorHandler kafkaErrorHandler() {
    DefaultErrorHandler errorHandler = getDefaultErrorHandler();

    errorHandler.addNotRetryableExceptions(RecordDeserializationException.class);

    errorHandler.setRetryListeners((record, ex, deliveryAttempt) -> {
      log.warn("Error on consumption attempt #{}. topic: {}, key:{}, record value: {}, exception: {}",
          deliveryAttempt, record.topic(), record.key(), record.value(), ex.getClass().getSimpleName());
      meterRegistry.counter("kafka.consumer.retry.attempts", "exception", ex.getClass().getSimpleName()).increment();
    });

    return errorHandler;
  }

  private DefaultErrorHandler getDefaultErrorHandler() {
    FixedBackOff backOff = new FixedBackOff(1000L, 3L);

    DefaultErrorHandler errorHandler = new DefaultErrorHandler(
        (record, ex) -> {
          log.error("Skipped poison message. topic: {}, key: {}, partition: {}, offset: {}, record value: {}",
              record.topic(), record.key(), record.partition(), record.offset(), record.value(), ex);
          meterRegistry.counter("kafka.consumer.skipped", "exception", ex.getClass().getSimpleName()).increment();
        },
        backOff
    );
    return errorHandler;
  }


}
