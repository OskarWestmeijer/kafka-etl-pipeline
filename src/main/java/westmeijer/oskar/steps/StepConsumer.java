package westmeijer.oskar.steps;

import io.cloudevents.CloudEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface StepConsumer {

  void consume(ConsumerRecord<String, CloudEvent> message);

}
