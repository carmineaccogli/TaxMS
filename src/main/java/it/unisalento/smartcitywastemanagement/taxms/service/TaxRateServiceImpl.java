package it.unisalento.smartcitywastemanagement.taxms.service;


import it.unisalento.smartcitywastemanagement.taxms.domain.TaxRate;
import it.unisalento.smartcitywastemanagement.taxms.repositories.TaxRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaxRateServiceImpl implements TaxRateService{


    @Autowired
    private TaxRateRepository taxRateRepository;


    public List<TaxRate> findAllTaxRates() {
        return taxRateRepository.findAll();
    }
}
