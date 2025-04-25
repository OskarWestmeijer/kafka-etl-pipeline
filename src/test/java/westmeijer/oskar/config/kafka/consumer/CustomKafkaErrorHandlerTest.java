package westmeijer.oskar.config.kafka.consumer;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.RecordDeserializationException;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;

class CustomKafkaErrorHandlerTest {

  @Test
  void shouldSeekAndCommitWhenRecordDeserializationExceptionThrown() {
    // given
    CustomKafkaErrorHandler errorHandler = new CustomKafkaErrorHandler();
    TopicPartition topicPartition = new TopicPartition("test-topic", 0);
    long offset = 5L;

    RecordDeserializationException exception = mock(RecordDeserializationException.class);
    given(exception.topicPartition()).willReturn(topicPartition);
    given(exception.offset()).willReturn(offset);

    Consumer<?, ?> consumer = mock(Consumer.class);
    willDoNothing().given(consumer).seek(topicPartition, offset + 1L);
    willDoNothing().given(consumer).commitSync();

    // when
    errorHandler.handle(exception, consumer);

    // then
    BDDMockito.then(consumer).should().seek(topicPartition, offset + 1);
    BDDMockito.then(consumer).should().commitSync();
  }

}