package westmeijer.oskar.steps;

import io.cloudevents.core.builder.CloudEventBuilder;
import java.net.URI;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CloudEventMetadata {

  /**
   * Assigning dataContentType to the CloudEvent, causes errors when deserializing with CloudEventDeserializer. The message is always parsed
   * in structured mode. The only way to bypass this, is not setting dataContentType.
   * <p>
   * Alternatively, it is possible to create a custom binary deserializer.
   */
  public static final CloudEventBuilder ceEventTemplate = CloudEventBuilder.v1()
      .withSource(URI.create("https://oskar-westmeijer.com"))
      .withType("products-ce-binary");

}
