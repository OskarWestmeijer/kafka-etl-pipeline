package westmeijer.oskar.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;

@Slf4j
public class KafkaErrorHandler implements CommonErrorHandler {

  @Override
  public boolean handleOne(Exception exception, ConsumerRecord<?, ?> record, Consumer<?, ?> consumer, MessageListenerContainer container) {
    log.info("handleOne");
    return handle(exception, record, consumer);
  }

  @Override
  public void handleOtherException(Exception exception, Consumer<?, ?> consumer, MessageListenerContainer container,
      boolean batchListener) {
    log.info("handleOtherException.");
    handle(exception, null, consumer);
  }

  private boolean handle(Exception exception, ConsumerRecord<?, ?> record, Consumer<?, ?> consumer) {
    var key = record != null ? record.key() : null;
    var value = record != null ? record.value() : null;
    log.error("Exception thrown on consumption. key: {}, value: {}", key, value, exception);
    return true;
  }

}
