package westmeijer.oskar.steps;

public enum Steps {

  PRODUCT_RECEIVER(null, Topics.PRODUCT_RECEIVED),
  CATEGORY_ASSIGNMENT(Topics.PRODUCT_RECEIVED, Topics.CATEGORY_ASSIGNED),
  PRICE_ASSIGNMENT(Topics.CATEGORY_ASSIGNED, Topics.PRICE_ASSIGNED),
  STOCK_ASSIGNMENT(Topics.PRICE_ASSIGNED, Topics.STOCK_ASSIGNED),
  PRODUCT_FINALIZER(Topics.STOCK_ASSIGNED, null);

  public final String inputTopic;
  public final String outputTopic;

  Steps(String inputTopic, String outputTopic) {
    this.inputTopic = inputTopic;
    this.outputTopic = outputTopic;
  }

  public static class Topics {

    public static final String PRODUCT_RECEIVED = "product-received";
    public static final String PRICE_ASSIGNED = "price-assigned";
    public static final String CATEGORY_ASSIGNED = "category-assigned";
    public static final String STOCK_ASSIGNED = "stock-assigned";
  }

}

