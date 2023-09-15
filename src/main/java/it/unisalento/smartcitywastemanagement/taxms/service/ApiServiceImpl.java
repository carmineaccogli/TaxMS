package it.unisalento.smartcitywastemanagement.taxms.service;


import it.unisalento.smartcitywastemanagement.taxms.dto.CitizenWasteMetricsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ApiServiceImpl implements ApiService {


    @Autowired
    WebClient disposalDataWebClient;

    public Mono<List<CitizenWasteMetricsDTO>> APICALL_getDisposalData(int year) {

        return disposalDataWebClient.get()
                .uri("/metrics/{year}", year)
                .retrieve()
                .bodyToFlux(CitizenWasteMetricsDTO.class)
                .collectList();
    }
}
