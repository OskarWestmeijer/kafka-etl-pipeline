package westmeijer.oskar.steps;

import westmeijer.oskar.config.kafka.MetricsDefinition;

public enum Steps {

  PRODUCT_RECEIVER(null, Topics.PRODUCT_RECEIVED, MetricsDefinition.PRODUCT_RECEIVED),
  CATEGORY_ASSIGNMENT(Topics.PRODUCT_RECEIVED, Topics.CATEGORY_ASSIGNED, MetricsDefinition.CATEGORY_ASSIGNED),
  PRICE_ASSIGNMENT(Topics.CATEGORY_ASSIGNED, Topics.PRICE_ASSIGNED, MetricsDefinition.PRICE_ASSIGNED),
  STOCK_ASSIGNMENT(Topics.PRICE_ASSIGNED, Topics.STOCK_ASSIGNED, MetricsDefinition.STOCK_ASSIGNED),
  PRODUCT_FINALIZER(Topics.STOCK_ASSIGNED, null, MetricsDefinition.PRODUCT_FINALIZED);

  public final String inputTopic;
  public final String outputTopic;
  public final String metricsDefinition;

  Steps(String inputTopic, String outputTopic, String metricsDefinition) {
    this.inputTopic = inputTopic;
    this.outputTopic = outputTopic;
    this.metricsDefinition = metricsDefinition;
  }

  public static class Topics {

    public static final String PRODUCT_RECEIVED = "product-received";
    public static final String PRICE_ASSIGNED = "price-assigned";
    public static final String CATEGORY_ASSIGNED = "category-assigned";
    public static final String STOCK_ASSIGNED = "stock-assigned";
  }

}

