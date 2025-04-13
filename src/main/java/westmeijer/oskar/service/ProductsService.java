package westmeijer.oskar.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import westmeijer.oskar.model.Product;
import westmeijer.oskar.steps.category.CategoryStepProducer;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductsService {

  private final CategoryStepProducer categoryTopicProducer;

  public void startProductProcessing(Product product) {
    categoryTopicProducer.produce(product);
  }

}
