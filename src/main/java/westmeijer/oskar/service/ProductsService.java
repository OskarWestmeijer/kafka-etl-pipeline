package westmeijer.oskar.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import westmeijer.oskar.service.model.Product;
import westmeijer.oskar.steps.receiver.ReceiverStepProcessor;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductsService {

  private final ReceiverStepProcessor receiverStepProcessor;

  public void startProductProcessing(Product product) {
    receiverStepProcessor.process(product);
  }

}
