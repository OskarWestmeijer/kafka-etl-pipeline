package westmeijer.oskar.steps.stock;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

public interface StockHttpClient {

  @GetExchange(url = "/stocks")
  StockResponse getStocks(@RequestParam("productId") Integer productId);

  record StockResponse(Integer stock) {

  }

}
