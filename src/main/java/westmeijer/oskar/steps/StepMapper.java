package westmeijer.oskar.steps;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.CloudEventUtils;
import io.cloudevents.core.data.PojoCloudEventData;
import io.cloudevents.jackson.PojoCloudEventDataMapper;
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

}
