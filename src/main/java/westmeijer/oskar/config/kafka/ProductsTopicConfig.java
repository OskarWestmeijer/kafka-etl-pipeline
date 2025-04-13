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
import westmeijer.oskar.steps.Steps.Topics;

@Configuration
public class ProductsTopicConfig {

  @Value(value = "${kafka.servers.products.bootstrap-server}")
  private String bootstrapAddress;

  @Bean
  public KafkaAdmin kafkaAdmin() {
    Map<String, Object> configs = new HashMap<>();
    configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    return new KafkaAdmin(configs);
  }

  @Bean
  public NewTopic categoryAssignmentTopic() {
    return TopicBuilder.name(Topics.CATEGORY)
        .partitions(1)
        .replicas(1)
        .build();
  }

  @Bean
  public NewTopic priceAssignmentTopic() {
    return TopicBuilder.name(Topics.PRICE)
        .partitions(1)
        .replicas(1)
        .build();
  }

  @Bean
  public NewTopic stockAssignmentTopic() {
    return TopicBuilder.name(Topics.STOCK)
        .partitions(1)
        .replicas(1)
        .build();
  }

}
