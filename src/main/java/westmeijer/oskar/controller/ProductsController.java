package westmeijer.oskar.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import westmeijer.oskar.controller.model.ProductRequest;
import westmeijer.oskar.controller.model.ProductResponse;
import westmeijer.oskar.service.ProductsService;
import westmeijer.oskar.service.model.Product;
import westmeijer.oskar.steps.receiver.ReceiverStepProcessor;

@Slf4j
@AllArgsConstructor
@RestController
public class ProductsController {

  private final ProductsService productsService;
  private final ReceiverStepProcessor receiverStepProcessor;

  @GetMapping("/ping")
  public ResponseEntity<String> ping() {
    log.info("Received ping request.");
    return new ResponseEntity<>("pong", HttpStatus.OK);
  }

  @GetMapping("/products/{id}")
  public ResponseEntity<ProductResponse> getProduct(@PathVariable Integer id) {
    log.info("Received GET request. id: {}", id);
    return productsService.getProduct(id)
        .map(this::map)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping("/products")
  public ResponseEntity<ProductRequest> addProduct(@RequestBody ProductRequest productRequest) {
    log.info("Receive POST request with body: {}", productRequest);
    var product = map(productRequest);
    receiverStepProcessor.process(product);
    return new ResponseEntity<>(productRequest, HttpStatus.ACCEPTED);
  }

  private Product map(ProductRequest productRequest) {
    return Product.builder()
        .id(productRequest.id())
        .name(productRequest.name())
        .build();
  }

  private ProductResponse map(Product product) {
    return ProductResponse.builder()
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
