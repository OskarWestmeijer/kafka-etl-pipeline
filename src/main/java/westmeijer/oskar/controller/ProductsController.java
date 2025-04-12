package westmeijer.oskar.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import westmeijer.oskar.model.Product;
import westmeijer.oskar.producer.ProductsCEBinaryProducer;
import westmeijer.oskar.producer.ProductsCEStructuredProducer;
import westmeijer.oskar.producer.ProductsProducer;

@Slf4j
@AllArgsConstructor
@RestController
public class ProductsController {

  private final ProductsProducer productsProducer;
  private final ProductsCEStructuredProducer productsCEStructuredProducer;
  private final ProductsCEBinaryProducer productsCEBinaryProducer;

  @GetMapping("/ping")
  public ResponseEntity<String> ping() {
    log.info("Received ping request.");
    return new ResponseEntity<>("pong", HttpStatus.OK);
  }

  // TODO: implement outbox pattern here
  @PostMapping("/products")
  public ResponseEntity<Product> addProduct(@RequestBody Product product) {
    log.info("Receive POST request with body: {}", product);
    productsProducer.sendMessage(product);
    productsCEStructuredProducer.sendMessage(product);
    productsCEBinaryProducer.sendMessage(product);
    return new ResponseEntity<>(product, HttpStatus.CREATED);
  }

}
