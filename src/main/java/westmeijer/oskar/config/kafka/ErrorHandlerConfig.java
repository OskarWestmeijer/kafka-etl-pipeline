package westmeijer.oskar.config.kafka;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.CommonErrorHandler;

@Configuration
public class ErrorHandlerConfig {

  @Bean
  CommonErrorHandler commonErrorHandler(MeterRegistry meterRegistry) {
    return new KafkaErrorHandlerImpl(meterRegistry);
  }

}
