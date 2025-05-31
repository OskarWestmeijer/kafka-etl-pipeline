package westmeijer.oskar.steps.price;

import java.math.BigDecimal;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

public interface PriceHttpClient {

  @GetExchange(url = "/prices")
  PriceResponse getPrices(@RequestParam("productId") Integer productId);

  record PriceResponse(BigDecimal price) {

  }

}
