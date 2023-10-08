package it.unisalento.smartcitywastemanagement.taxms.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class WebClientConfiguration {

    private final String API_DISPOSAL_MS = "http://disposalManagementService:8080/api/disposal";

    private final String API_CITIZEN_MS="http://citizenManagementService:8080/api/citizen";

    @Bean
    public WebClient disposalDataWebClient(WebClient.Builder webClientBuilder) {

        ExchangeFilterFunction errorHandlingFilter = ExchangeFilterFunction.ofResponseProcessor(
                clientResponse -> {
                    if (!clientResponse.statusCode().is2xxSuccessful()) {
                        return clientResponse.createException()
                                .flatMap(Mono::error);
                    }
                    return Mono.just(clientResponse);
                });

        return webClientBuilder
                .baseUrl(API_DISPOSAL_MS)
                .filter(errorHandlingFilter)
                .build();
    }


    @Bean
    public WebClient citizenDataWebClient(WebClient.Builder webClientBuilder) {

        ExchangeFilterFunction errorHandlingFilter = ExchangeFilterFunction.ofResponseProcessor(
                clientResponse -> {
                    if (!clientResponse.statusCode().is2xxSuccessful()) {
                        return clientResponse.createException()
                                .flatMap(Mono::error);
                    }
                    return Mono.just(clientResponse);
                });

        return webClientBuilder
                .baseUrl(API_CITIZEN_MS)
                .filter(errorHandlingFilter)
                .build();
    }
}
