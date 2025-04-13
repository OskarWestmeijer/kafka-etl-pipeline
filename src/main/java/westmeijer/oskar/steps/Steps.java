package westmeijer.oskar.steps;

public enum Steps {

  CATEGORY_ASSIGNMENT(Topics.CATEGORY, Topics.PRICE),
  PRICE_ASSIGNMENT(Topics.PRICE, null);

  public final String inputTopic;
  public final String outputTopic;

  private Steps(String inputTopic, String outputTopic) {
    this.inputTopic = inputTopic;
    this.outputTopic = outputTopic;
  }

  public static class Topics {

    public static final String CATEGORY = "category-assignment";
    public static final String PRICE = "price-assignment";
  }

}

