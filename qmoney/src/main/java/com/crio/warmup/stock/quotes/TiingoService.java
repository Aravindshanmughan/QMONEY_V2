
package com.crio.warmup.stock.quotes;

import com.crio.warmup.stock.PortfolioManagerApplication;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class TiingoService implements StockQuotesService {

RestTemplate restTemplate;
private ObjectMapper om = getObjectMapper();

  protected TiingoService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to) {
    String uri = buildUri(symbol, from, to);
    try {
      String result = restTemplate.getForObject(uri, String.class);
      List<TiingoCandle> tiingoCandles = om.readValue(result, new TypeReference<>() {});

      // Convert TiingoCandle objects to Candle objects
      List<Candle> candles = new ArrayList<>();
      for (TiingoCandle tiingoCandle : tiingoCandles) {
        candles.add(tiingoCandle);
      }
      return candles;
    } catch (RestClientException | JsonProcessingException e) {
      // Log the exception or handle it appropriately
      e.printStackTrace();
      // Return an empty list or null to indicate failure
      return Collections.emptyList();
    }
  }

  


      protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
        // String uriTemplate = "https:api.tiingo.com/tiingo/daily/$SYMBOL/prices?"
        //      + "startDate=$STARTDATE&endDate=$ENDDATE&token=$APIKEY";
             String token=PortfolioManagerApplication.getToken();
             String baseUrl = "https://api.tiingo.com/tiingo/daily"; 
             String url = baseUrl + "/" + symbol + "/prices?startDate=" + startDate.toString() + "&endDate=" + endDate.toString() + "&token=" + token;
             return url;
   }

   private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }



  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement getStockQuote method below that was also declared in the interface.

  // Note:
  // 1. You can move the code from PortfolioManagerImpl#getStockQuote inside newly created method.
  // 2. Run the tests using command below and make sure it passes.
  //    ./gradlew test --tests TiingoServiceTest


  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Write a method to create appropriate url to call the Tiingo API.

}
