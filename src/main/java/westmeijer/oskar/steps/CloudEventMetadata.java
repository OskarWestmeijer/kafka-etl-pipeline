package westmeijer.oskar.steps;

import io.cloudevents.core.builder.CloudEventBuilder;
import java.net.URI;

public class CloudEventMetadata {

  /**
   * Assigning dataContentType to the CloudEvent, causes errors when deserializing with CloudEventDeserializer. The message is always parsed
   * in structured mode. The only way to bypass this, is not setting dataContentType.
   * <p>
   * Alternatively, I created a custom implementation, using the correct binary cloud event deserializer.
   * CustomBinaryCloudEventDeserializer.
   */
  public static final CloudEventBuilder ceEventTemplate = CloudEventBuilder.v1()
      .withSource(URI.create("https://oskar-westmeijer.com"))
      .withType("products-ce-binary");

}
