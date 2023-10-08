package it.unisalento.smartcitywastemanagement.taxms.service;


import it.unisalento.smartcitywastemanagement.taxms.dto.CitizenWasteMetricsDTO;
import it.unisalento.smartcitywastemanagement.taxms.security.JwtUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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

    @Autowired
    WebClient citizenDataWebClient;


    @Autowired
    private JwtUtilities jwtUtilities;

    public Mono<List<CitizenWasteMetricsDTO>> APICALL_getDisposalData(int year) {

        final String jwtToken = jwtUtilities.generateToken();

        return disposalDataWebClient.get()
                .uri("/metrics/year/{year}", year)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .retrieve()
                .bodyToFlux(CitizenWasteMetricsDTO.class)
                .collectList();
    }

    public Mono<List<String>> APICALL_getCitizenData() {

        final String jwtToken = jwtUtilities.generateToken();

        return disposalDataWebClient.get()
                .uri("/")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .retrieve()
                .bodyToFlux(String.class)
                .collectList();
    }
}
