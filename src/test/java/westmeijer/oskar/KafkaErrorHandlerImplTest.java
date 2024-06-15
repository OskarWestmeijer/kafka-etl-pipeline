package westmeijer.oskar;

import static org.assertj.core.api.BDDAssertions.then;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.RecordDeserializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.listener.MessageListenerContainer;
import westmeijer.oskar.config.kafka.KafkaErrorHandlerImpl;

@ExtendWith(MockitoExtension.class)
public class KafkaErrorHandlerImplTest {

  @Mock
  private Consumer<String, String> consumer;

  @Mock
  private ConsumerRecord<String, String> record;

  @Mock
  private MessageListenerContainer container;

  @Captor
  private ArgumentCaptor<Long> offsetCaptor;

  private MeterRegistry meterRegistry;
  private KafkaErrorHandlerImpl errorHandler;

  @BeforeEach
  void setUp() {
    meterRegistry = new SimpleMeterRegistry();
    errorHandler = new KafkaErrorHandlerImpl(meterRegistry);
  }

  @Test
  void shouldHandleOne() {
    Exception exception = new Exception("Test Exception");

    boolean result = errorHandler.handleOne(exception, record, consumer, container);

    then(result).isTrue();
    then(meterRegistry.counter("consumption.error").count()).isEqualTo(1);
    BDDMockito.then(consumer).shouldHaveNoInteractions();
  }

  @Test
  void shouldHandleOtherException() {
    Exception exception = new Exception("Test Exception");

    errorHandler.handleOtherException(exception, consumer, container, true);

    then(meterRegistry.counter("consumption.error").count()).isEqualTo(1);
    BDDMockito.then(consumer).shouldHaveNoInteractions();
  }

  @Test
  void shouldHandleRecordDeserializationException() {
    TopicPartition topicPartition = new TopicPartition("test-topic", 0);
    long offset = 100L;
    RecordDeserializationException rde = new RecordDeserializationException(
        topicPartition, offset, "Error deserializing record", new IllegalArgumentException());

    boolean result = errorHandler.handleOne(rde, record, consumer, container);

    then(result).isTrue();
    then(meterRegistry.counter("consumption.error").count()).isEqualTo(1);
    BDDMockito.then(consumer).should().seek(topicPartition, offset + 1);
    BDDMockito.then(consumer).should().commitSync();
  }

}
