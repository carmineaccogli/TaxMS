package it.unisalento.smartcitywastemanagement.taxms.service;

import it.unisalento.smartcitywastemanagement.taxms.domain.CitizenTaxStatus;
import it.unisalento.smartcitywastemanagement.taxms.exceptions.CitizenNotFoundException;

import java.util.List;

public interface CitizenTaxStatusService {



    List<CitizenTaxStatus> findAllTaxStatus();

    CitizenTaxStatus findTaxStatusByCitizen(String citizenID);

    void updateCitizenTaxStatus(String citizenID,boolean status) throws CitizenNotFoundException;
}
