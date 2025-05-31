package westmeijer.oskar.config.httpclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import westmeijer.oskar.steps.category.CategoryHttpClient;
import westmeijer.oskar.steps.price.PriceHttpClient;
import westmeijer.oskar.steps.stock.StockHttpClient;

@Configuration
@Slf4j
public class HttpClientConfig {

  @Bean
  CategoryHttpClient categoryHttpClient() {
    RestClient restClient = RestClient.builder()
        .baseUrl("http://localhost:9000")
        .build();

    RestClientAdapter adapter = RestClientAdapter.create(restClient);
    HttpServiceProxyFactory factory = HttpServiceProxyFactory
        .builderFor(adapter)
        .build();
    return factory.createClient(CategoryHttpClient.class);
  }

  @Bean
  StockHttpClient stockHttpClient() {
    RestClient restClient = RestClient.builder()
        .baseUrl("http://localhost:9000")
        .build();

    RestClientAdapter adapter = RestClientAdapter.create(restClient);
    HttpServiceProxyFactory factory = HttpServiceProxyFactory
        .builderFor(adapter)
        .build();
    return factory.createClient(StockHttpClient.class);
  }

  @Bean
  PriceHttpClient priceHttpClient() {
    RestClient restClient = RestClient.builder()
        .baseUrl("http://localhost:9000")
        .build();

    RestClientAdapter adapter = RestClientAdapter.create(restClient);
    HttpServiceProxyFactory factory = HttpServiceProxyFactory
        .builderFor(adapter)
        .build();
    return factory.createClient(PriceHttpClient.class);
  }

}
