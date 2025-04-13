package westmeijer.oskar.consumer;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.junit.jupiter.MockitoExtension;
import westmeijer.oskar.service.model.Product;
import westmeijer.oskar.steps.stock.StockStepConsumer;

@ExtendWith(MockitoExtension.class)
public class ProductsStepConsumerTest {


  private final MeterRegistry meterRegistry = new SimpleMeterRegistry();
  private final Validator validator = mock(Validator.class);
  private StockStepConsumer stockStepConsumer;

  @BeforeEach
  void setup() {
    stockStepConsumer = new StockStepConsumer(validator, meterRegistry);
    meterRegistry.clear();
    stockStepConsumer.clearLastMessage();
  }

  @Test
  void shouldConsumeMessage() {
    Product product = new Product(1234, "Java by Comparison");
    ConsumerRecord<String, Product> consumerRecord = mock(ConsumerRecord.class);
    given(consumerRecord.value()).willReturn(product);

    stockStepConsumer.listenToProducts(consumerRecord);
    then(stockStepConsumer.getLatestMsg()).isEqualTo(product);
    then(meterRegistry.counter("products.consumed").count()).isEqualTo(1d);
    BDDMockito.then(validator).should().validate(product);
  }

  @Test
  void shouldThrowOnInvalidMessage() {
    Product product = new Product(-1234, "Java by Comparison");
    ConsumerRecord<String, Product> consumerRecord = mock(ConsumerRecord.class);
    given(consumerRecord.value()).willReturn(product);

    ConstraintViolation<Product> violation = mock(ConstraintViolation.class);
    given(validator.validate(product)).willReturn(Set.of(violation));

    thenThrownBy(() -> stockStepConsumer.listenToProducts(consumerRecord))
        .isInstanceOf(IllegalArgumentException.class);

    then(stockStepConsumer.getLatestMsg()).isNull();
    then(meterRegistry.counter("products.consumed").count()).isEqualTo(0d);
    BDDMockito.then(validator).should().validate(product);
  }

  @Test
  void shouldClearLastMessage() {
    Product product = new Product(1234, "Java by Comparison");
    ConsumerRecord<String, Product> consumerRecord = mock(ConsumerRecord.class);
    given(consumerRecord.value()).willReturn(product);

    stockStepConsumer.listenToProducts(consumerRecord);
    then(stockStepConsumer.getLatestMsg()).isEqualTo(product);
    then(meterRegistry.counter("products.consumed").count()).isEqualTo(1d);

    stockStepConsumer.clearLastMessage();
    then(stockStepConsumer.getLatestMsg()).isNull();

    BDDMockito.then(validator).should().validate(product);
  }

}
