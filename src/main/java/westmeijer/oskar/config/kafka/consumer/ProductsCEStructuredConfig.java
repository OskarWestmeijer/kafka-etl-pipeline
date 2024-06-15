package westmeijer.oskar.config.kafka.consumer;

import io.cloudevents.CloudEvent;
import io.cloudevents.kafka.CloudEventDeserializer;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;


@Slf4j
@Configuration
public class ProductsCEStructuredConfig {


  @Value(value = "${kafka.servers.products.bootstrap-server}")
  private String bootstrapAddress;

  @Value(value = "${kafka.servers.products.consumers.products-ce-structured.group-id}")
  private String groupId;

  @Bean
  ConsumerFactory<String, CloudEvent> productsCEStructuredConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, CloudEventDeserializer.class);
    return new DefaultKafkaConsumerFactory<>(
        props,
        new StringDeserializer(),
        new CloudEventDeserializer()
    );
  }

  @Bean
  ConcurrentKafkaListenerContainerFactory<String, CloudEvent> kafkaListenerContainerFactory(CommonErrorHandler commonErrorHandler) {
    var factory = new ConcurrentKafkaListenerContainerFactory<String, CloudEvent>();
    factory.setConsumerFactory(productsCEStructuredConsumerFactory());
    factory.setCommonErrorHandler(commonErrorHandler);
    return factory;
  }

}
