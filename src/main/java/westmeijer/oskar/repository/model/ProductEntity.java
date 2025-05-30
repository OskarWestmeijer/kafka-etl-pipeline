package westmeijer.oskar.repository.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ProductEntity {

  @Id
  private Integer id;

  private String name;

  private String category;

  private BigDecimal price;

  private Integer stock;

  @CreationTimestamp
  private Instant createdAt;

  @UpdateTimestamp
  private Instant lastModifiedAt;

  private Instant lastFinalizedAt;

}
