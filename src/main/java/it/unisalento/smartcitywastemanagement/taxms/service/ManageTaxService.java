package it.unisalento.smartcitywastemanagement.taxms.service;

import it.unisalento.smartcitywastemanagement.taxms.domain.Tax;
import it.unisalento.smartcitywastemanagement.taxms.exceptions.AnnualTaxAlreadyEmittedException;
import it.unisalento.smartcitywastemanagement.taxms.exceptions.CitizenNotFoundException;
import it.unisalento.smartcitywastemanagement.taxms.exceptions.TaxRateNotFoundException;

import java.util.List;

public interface ManageTaxService {


    List<Tax> findTaxesOfCitizen(String citizenID);

    List<String> emitTaxes() throws AnnualTaxAlreadyEmittedException, TaxRateNotFoundException;
}