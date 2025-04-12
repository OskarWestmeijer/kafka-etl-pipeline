package westmeijer.oskar.config.kafka.producer;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.message.Encoding;
import io.cloudevents.jackson.JsonFormat;
import io.cloudevents.kafka.CloudEventSerializer;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class ProductsCEProducerConfig {

  @Value(value = "${kafka.servers.products.bootstrap-server}")
  private String bootstrapAddress;

  private ProducerFactory<String, CloudEvent> productsCEProducerFactory(Encoding cloudEventEncoding) {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    configProps.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CloudEventSerializer.class);
    configProps.put(CloudEventSerializer.ENCODING_CONFIG, cloudEventEncoding);
    configProps.put(CloudEventSerializer.EVENT_FORMAT_CONFIG, JsonFormat.CONTENT_TYPE);
    return new DefaultKafkaProducerFactory<>(configProps);
  }

  @Bean(value = "productsCEStructuredKafkaTemplate")
  public KafkaTemplate<String, CloudEvent> productsCEStructuredKafkaTemplate() {
    return new KafkaTemplate<>(productsCEProducerFactory(Encoding.STRUCTURED));
  }

  @Bean(value = "productsCEBinaryKafkaTemplate")
  public KafkaTemplate<String, CloudEvent> productsCEBinaryKafkaTemplate() {
    return new KafkaTemplate<>(productsCEProducerFactory(Encoding.BINARY));
  }

}
