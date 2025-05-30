package westmeijer.oskar.controller.model;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.Builder;

@Builder
public record ProductResponse(
    Integer id,
    String name,
    String category,
    BigDecimal price,
    Integer stock,
    Instant createdAt,
    Instant lastModifiedAt,
    Instant lastFinalizedAt
) {

}
