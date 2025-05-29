package westmeijer.oskar.steps.category;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

public interface CategoryHttpClient {

  @GetExchange(url = "/category")
  CategoryResponse getCategory(@RequestParam("productId") Integer productId);

  record CategoryResponse(String category) {

  }

}
