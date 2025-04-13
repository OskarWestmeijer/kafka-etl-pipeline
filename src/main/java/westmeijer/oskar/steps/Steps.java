package westmeijer.oskar.steps;

public enum Steps {

  KICK_OFF(null, Topics.CATEGORY),
  CATEGORY_ASSIGNMENT(Topics.CATEGORY, Topics.PRICE),
  PRICE_ASSIGNMENT(Topics.PRICE, Topics.STOCK),
  STOCK_ASSIGNMENT(Topics.STOCK, null);

  public final String inputTopic;
  public final String outputTopic;

  Steps(String inputTopic, String outputTopic) {
    this.inputTopic = inputTopic;
    this.outputTopic = outputTopic;
  }

  public static class Topics {

    public static final String CATEGORY = "category-assignment";
    public static final String PRICE = "price-assignment";
    public static final String STOCK = "stock-assignment";
  }

}

