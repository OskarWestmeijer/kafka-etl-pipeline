package westmeijer.oskar.steps;

import static java.util.Objects.requireNonNull;
import static westmeijer.oskar.steps.CloudEventMetadata.ceEventTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.CloudEventUtils;
import io.cloudevents.core.data.PojoCloudEventData;
import io.cloudevents.jackson.PojoCloudEventDataMapper;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import westmeijer.oskar.service.model.Product;

@Slf4j
@Component
@RequiredArgsConstructor
public class StepMapper {

  private final ObjectMapper objectMapper;

  public Product map(CloudEvent cloudEvent) {
    PojoCloudEventData<Product> deserializedData = CloudEventUtils
        .mapData(cloudEvent, PojoCloudEventDataMapper.from(objectMapper, Product.class));
    requireNonNull(deserializedData, "message data is required");
    return deserializedData.getValue();
  }

  public CloudEvent map(Product product) {
    String productJson;
    try {
      productJson = objectMapper.writeValueAsString(product);
    } catch (Exception exception) {
      log.error("Error on serialization.");
      throw new RuntimeException(exception);
    }

    return ceEventTemplate
        .withId(UUID.randomUUID().toString())
        .withTime(OffsetDateTime.ofInstant(Instant.now(), ZoneId.of("Europe/Berlin")))
        .withData(productJson.getBytes(StandardCharsets.UTF_8))
        .build();
  }

}
