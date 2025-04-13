package westmeijer.oskar.config.kafka.consumer;

import io.cloudevents.CloudEvent;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.format.EventDeserializationException;
import io.cloudevents.kafka.impl.KafkaBinaryMessageReaderImpl;
import io.cloudevents.kafka.impl.KafkaHeaders;
import java.util.Map;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;

public class CustomBinaryCloudEventDeserializer implements Deserializer<CloudEvent> {

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {
    // throw new UnsupportedOperationException("CloudEventDeserializer supports only the signature deserialize(String, Headers, byte[])");
  }

  @Override
  public CloudEvent deserialize(String s, byte[] bytes) {
    throw new UnsupportedOperationException("CloudEventDeserializer supports only the signature deserialize(String, Headers, byte[])");
  }

  @Override
  public CloudEvent deserialize(String topic, Headers headers, byte[] data) {
    var specVersionUnparsed = KafkaHeaders.getParsedKafkaHeader(headers, KafkaHeaders.SPEC_VERSION);
    if (specVersionUnparsed != null) {
      var specVersion = SpecVersion.parse(specVersionUnparsed);
      var binaryMessageReader = new KafkaBinaryMessageReaderImpl(specVersion, headers, data);
      var cloudEvent = binaryMessageReader.toEvent();
      return cloudEvent;
    }
    throw new EventDeserializationException(new RuntimeException("Custom binary parsing failed."));
  }

}
