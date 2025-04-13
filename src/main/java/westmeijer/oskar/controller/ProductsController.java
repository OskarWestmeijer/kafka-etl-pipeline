package westmeijer.oskar.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import westmeijer.oskar.controller.model.ProductRequest;
import westmeijer.oskar.model.Product;
import westmeijer.oskar.service.ProductsService;

@Slf4j
@AllArgsConstructor
@RestController
public class ProductsController {

  private final ProductsService productsService;

  @GetMapping("/ping")
  public ResponseEntity<String> ping() {
    log.info("Received ping request.");
    return new ResponseEntity<>("pong", HttpStatus.OK);
  }

  @PostMapping("/products")
  public ResponseEntity<ProductRequest> addProduct(@RequestBody ProductRequest productRequest) {
    log.info("Receive POST request with body: {}", productRequest);
    var product = map(productRequest);
    productsService.startProductProcessing(product);
    return new ResponseEntity<>(productRequest, HttpStatus.ACCEPTED);
  }

  private Product map(ProductRequest productRequest) {
    return Product.builder()
        .id(productRequest.id())
        .name(productRequest.name())
        .build();
  }

}
