package westmeijer.oskar.service.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import lombok.Builder;
import org.apache.commons.lang3.ObjectUtils;

@Builder(toBuilder = true)
public record Product(Integer id,
                      String name,
                      String category,
                      BigDecimal price,
                      Integer stock,
                      Instant createdAt,
                      Instant lastModifiedAt,
                      Instant lastFinalizedAt) implements Serializable {

  public Product {
    Objects.requireNonNull(id, "id cannot be null");
    ObjectUtils.requireNonEmpty(name, "name cannot be null or empty");
  }
}
