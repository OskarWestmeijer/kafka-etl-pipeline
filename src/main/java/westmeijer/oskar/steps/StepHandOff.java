package westmeijer.oskar.steps;

import westmeijer.oskar.service.model.Product;

public interface StepHandOff {

  void produce(Product product);

  String getOutgoingTopic();

}
