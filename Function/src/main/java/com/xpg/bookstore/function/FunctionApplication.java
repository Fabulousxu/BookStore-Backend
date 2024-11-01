package com.xpg.bookstore.function;

import java.util.Arrays;
import java.util.function.Function;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class FunctionApplication {

  public static void main(String[] args) {
    SpringApplication.run(FunctionApplication.class, args);
  }

  @Bean
  public Function<Flux<int[]>, Flux<Integer>> calSingleTotalPrice() {
    return flux -> flux.map(value -> value[0] * value[1]);
  }

  @Bean
  public Function<Flux<int[]>, Flux<Integer>> calAllTotalPrice() {
    return flux -> flux.map(value -> Arrays.stream(value).sum());
  }
}
