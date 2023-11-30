
package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;
import com.crio.warmup.stock.PortfolioManagerApplication;
import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {




  private RestTemplate restTemplate;


  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }


  //TODO: CRIO_TASK_MODULE_REFACTOR
  // 1. Now we want to convert our code into a module, so we will not call it from main anymore.
  //    Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  //    into #calculateAnnualizedReturn function here and ensure it follows the method signature.
  // 2. Logic to read Json file and convert them into Objects will not be required further as our
  //    clients will take care of it, going forward.

  // Note:
  // Make sure to exercise the tests inside PortfolioManagerTest using command below:
  // ./gradlew test --tests PortfolioManagerTest

  //CHECKSTYLE:OFF




  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo third-party APIs to a separate function.
  //  Remember to fill out the buildUri function and use that.


  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException {
        String url=buildUri(symbol, from, to);
        ResponseEntity<TiingoCandle[]> response = restTemplate.getForEntity(url, TiingoCandle[].class);

        if (response.getStatusCode() == HttpStatus.OK) {
            TiingoCandle[] candlesArray = response.getBody();
            if (candlesArray != null) {
                return Arrays.asList(candlesArray);
            }
        }
    
        return Collections.emptyList();
      }
  

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
       String uriTemplate = "https:api.tiingo.com/tiingo/daily/$SYMBOL/prices?"
            + "startDate=$STARTDATE&endDate=$ENDDATE&token=$APIKEY";
            String token=PortfolioManagerApplication.getToken();
            String baseUrl = "https://api.tiingo.com/tiingo/daily"; 
            String url = baseUrl + "/" + symbol + "/prices?startDate=" + startDate.toString() + "&endDate=" + endDate.toString() + "&token=" + token;
            return url;
  }
  



  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades,
      LocalDate endDate) {

        List<AnnualizedReturn> annualizedReturns = new ArrayList<>();
        for (PortfolioTrade trade : portfolioTrades) {
          String symbol = trade.getSymbol();
          LocalDate purchaseDate = trade.getPurchaseDate();
          
          try {
            List<Candle> stockQuotes = getStockQuote(symbol, purchaseDate, endDate);
            if (!stockQuotes.isEmpty()) {
              double buyPrice = stockQuotes.get(0).getOpen();
              double sellPrice = stockQuotes.get(stockQuotes.size() - 1).getClose();
              double totalReturn = (sellPrice - buyPrice) / buyPrice;
              double totalDays = DAYS.between(purchaseDate, endDate);
              
              double annualizedReturn = Math.pow(1 + totalReturn, 365 / totalDays) - 1;
              AnnualizedReturn result = new AnnualizedReturn(symbol, annualizedReturn, totalReturn);
              annualizedReturns.add(result);
            

        }
            else {

              System.out.println("No stock quotes available for symbol: " + symbol);
             }
            }catch (JsonProcessingException e) {
              e.printStackTrace();
       
  }
}
        

  annualizedReturns.sort(getComparator());
    return annualizedReturns;

          
    
  }
}
      
    
      
    
  
      