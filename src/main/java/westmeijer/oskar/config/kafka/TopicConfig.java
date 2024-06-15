package westmeijer.oskar.config.kafka;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class TopicConfig {

  @Value(value = "${kafka.servers.products.bootstrap-server}")
  private String bootstrapAddress;

  @Value(value = "${kafka.servers.products.consumers.products.topic-name}")
  private String productsTopic;

  @Value(value = "${kafka.servers.products.consumers.products-ce-structured.topic-name}")
  private String productsCEStructuredTopic;

  @Bean
  public KafkaAdmin kafkaAdmin() {
    Map<String, Object> configs = new HashMap<>();
    configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    return new KafkaAdmin(configs);
  }

  @Bean
  public NewTopic productsTopic() {
    return TopicBuilder.name(productsTopic)
        .partitions(3)
        .replicas(1)
        .build();
  }

  @Bean
  public NewTopic productsCEStructuredTopic() {
    return TopicBuilder.name(productsCEStructuredTopic)
        .partitions(3)
        .replicas(1)
        .build();
  }

}
