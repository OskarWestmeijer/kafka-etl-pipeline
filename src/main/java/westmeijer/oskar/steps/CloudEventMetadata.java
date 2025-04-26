package westmeijer.oskar.steps;

import io.cloudevents.core.builder.CloudEventBuilder;
import java.net.URI;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.MediaType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CloudEventMetadata {

  /**
   * Documentation: <a
   * href="https://github.com/cloudevents/spec/blob/main/cloudevents/bindings/kafka-protocol-binding.md#kafka-protocol-binding-for-cloudevents---version-103-wip">Kafka
   * CloudEvents</a>
   */
  public static final CloudEventBuilder ceEventTemplate = CloudEventBuilder.v1()
      .withSource(URI.create("/products"))
      .withType("product.received")
      .withDataContentType(MediaType.APPLICATION_JSON_VALUE);
}
