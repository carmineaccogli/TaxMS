package it.unisalento.smartcitywastemanagement.taxms.service;

import it.unisalento.smartcitywastemanagement.taxms.dto.CitizenWasteMetricsDTO;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ApiService {


    Mono<List<CitizenWasteMetricsDTO>> APICALL_getDisposalData(int year);

    Mono<String[]> APICALL_getCitizenData();
}
