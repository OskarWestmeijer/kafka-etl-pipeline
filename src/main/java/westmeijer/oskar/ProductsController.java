package westmeijer.oskar;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@RestController
public class ProductsController {

    private final ProductsProducer productsProducer;

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        log.info("Received ping request.");
        return new ResponseEntity<>("pong", HttpStatus.OK);
    }

    @PostMapping("/products")
    public ResponseEntity<Product> addProduct(@Valid @RequestBody Product product) {
        log.info("Receive POST request with body: {}", product);
        productsProducer.sendMessage(product);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

}
