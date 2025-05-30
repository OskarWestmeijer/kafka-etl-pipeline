package westmeijer.oskar.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import westmeijer.oskar.repository.ProductRepository;
import westmeijer.oskar.service.model.Product;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductsService {

  private final ProductRepository productRepository;

  public Product saveProduct(Product product) {
    return productRepository.save(product);
  }

  public Optional<Product> getProduct(Integer id) {
    var optionalProduct = productRepository.getProduct(id);
    log.info("Fetched product: {}", optionalProduct);
    return optionalProduct;
  }

}
