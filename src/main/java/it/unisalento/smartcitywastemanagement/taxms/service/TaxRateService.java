package it.unisalento.smartcitywastemanagement.taxms.service;

import it.unisalento.smartcitywastemanagement.taxms.domain.TaxRate;

import java.util.List;

public interface TaxRateService {


    List<TaxRate> findAllTaxRates();
}
