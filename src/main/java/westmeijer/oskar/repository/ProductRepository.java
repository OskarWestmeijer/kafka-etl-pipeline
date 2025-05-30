package westmeijer.oskar.repository;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import westmeijer.oskar.repository.model.ProductEntity;
import westmeijer.oskar.service.model.Product;

@Repository
@RequiredArgsConstructor
public class ProductRepository {

  private final ProductJpaRepository productJpaRepository;

  public Optional<Product> getProduct(Integer id) {
    return productJpaRepository.findById(id)
        .map(this::map);
  }

  public Product save(Product product) {
    var existing = productJpaRepository.findById(product.id());
    ProductEntity entity;

    if (existing.isPresent()) {
      // Preserve fields like createdAt from existing entity
      var existingEntity = existing.get();
      entity = map(product);
      entity.setCreatedAt(existingEntity.getCreatedAt());
    } else {
      entity = map(product);
    }

    var savedEntity = productJpaRepository.save(entity);
    return map(savedEntity);
  }

  private Product map(ProductEntity entity) {
    return Product.builder()
        .id(entity.getId())
        .name(entity.getName())
        .category(entity.getCategory())
        .price(entity.getPrice())
        .stock(entity.getStock())
        .createdAt(entity.getCreatedAt())
        .lastModifiedAt(entity.getLastModifiedAt())
        .lastFinalizedAt(entity.getLastFinalizedAt())
        .build();
  }

  private ProductEntity map(Product product) {
    return ProductEntity.builder()
        .id(product.id())
        .name(product.name())
        .category(product.category())
        .price(product.price())
        .stock(product.stock())
        .createdAt(product.createdAt())
        .lastModifiedAt(product.lastModifiedAt())
        .lastFinalizedAt(product.lastFinalizedAt())
        .build();
  }

}
