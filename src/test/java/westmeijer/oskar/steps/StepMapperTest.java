package westmeijer.oskar.steps;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import westmeijer.oskar.service.model.Product;

@ExtendWith(MockitoExtension.class)
class StepMapperTest {

  @Mock
  private ObjectMapper objectMapper;

  @InjectMocks
  private StepMapper stepMapper;

  @Test
  @SneakyThrows
  void shouldMapProductToCloudEvent() {
    // given
    Product product = mock(Product.class);
    String productJson = "mapped";
    given(objectMapper.writeValueAsString(product)).willReturn(productJson);

    // when
    CloudEvent actual = stepMapper.map(product);

    // then
    then(new String(actual.getData().toBytes())).isEqualTo(productJson);
  }

  @Test
  @SneakyThrows
  void shouldThrowOnMapProductToCloudEvent() {
    // given

    Product product = mock(Product.class);
    JsonProcessingException e = new JsonProcessingException("bad luck") {
    };
    given(objectMapper.writeValueAsString(product)).willThrow(e);

    // when & then
    thenThrownBy(() -> stepMapper.map(product))
        .isInstanceOf(RuntimeException.class)
        .hasRootCauseInstanceOf(JsonProcessingException.class)
        .hasMessageContaining("bad luck");
  }

}