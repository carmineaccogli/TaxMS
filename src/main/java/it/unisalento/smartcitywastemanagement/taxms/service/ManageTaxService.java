package it.unisalento.smartcitywastemanagement.taxms.service;

import it.unisalento.smartcitywastemanagement.taxms.domain.Tax;
import it.unisalento.smartcitywastemanagement.taxms.exceptions.AnnualTaxAlreadyEmittedException;
import it.unisalento.smartcitywastemanagement.taxms.exceptions.CitizenNotFoundException;
import it.unisalento.smartcitywastemanagement.taxms.exceptions.TaxNotFoundException;
import it.unisalento.smartcitywastemanagement.taxms.exceptions.TaxRateNotFoundException;

import java.util.List;
import java.util.Map;

public interface ManageTaxService {


    List<Tax> findTaxesOfCitizen(String citizenID);

    List<String> emitTaxes(Map<String,Double> feeMultiplierByType)  throws AnnualTaxAlreadyEmittedException, TaxRateNotFoundException;

    boolean checkTaxToPay(String citizenID);

    boolean checkAlreadyEmitted(int year);

    Tax findTaxByID(String taxID) throws TaxNotFoundException;
}
