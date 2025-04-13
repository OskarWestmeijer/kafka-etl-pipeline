package westmeijer.oskar.config.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.errors.RecordDeserializationException;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;

@Slf4j
public class CustomKafkaErrorHandler implements CommonErrorHandler {

  @Override
  public boolean handleOne(Exception exception, ConsumerRecord<?, ?> record, Consumer<?, ?> consumer, MessageListenerContainer container) {
    handle(exception, consumer);
    return false;
  }

  @Override
  public void handleOtherException(Exception exception, Consumer<?, ?> consumer, MessageListenerContainer container,
      boolean batchListener) {
    handle(exception, consumer);
  }

  void handle(Exception exception, Consumer<?, ?> consumer) {
    log.error("Deserialization exception thrown.", exception);
    if (exception instanceof RecordDeserializationException ex) {
      var topic = ex.topicPartition().topic();
      consumer.seek(ex.topicPartition(), ex.offset() + 1L);
      consumer.commitSync();
      log.info("Skipped message. topic: {}, exception type: {}", topic, ex.getClass());
    }
  }

}
