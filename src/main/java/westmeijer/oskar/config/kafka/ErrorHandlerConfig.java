package westmeijer.oskar.config.kafka;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.CommonErrorHandler;

@Configuration
public class ErrorHandlerConfig {

  @Bean
  CommonErrorHandler commonErrorHandler() {
    return new KafkaErrorHandlerImpl();
  }

}
