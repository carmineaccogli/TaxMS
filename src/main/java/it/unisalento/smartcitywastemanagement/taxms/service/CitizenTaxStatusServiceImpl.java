package it.unisalento.smartcitywastemanagement.taxms.service;


import it.unisalento.smartcitywastemanagement.taxms.domain.CitizenTaxStatus;
import it.unisalento.smartcitywastemanagement.taxms.exceptions.CitizenNotFoundException;
import it.unisalento.smartcitywastemanagement.taxms.repositories.CitizenTaxStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CitizenTaxStatusServiceImpl implements CitizenTaxStatusService{

    @Autowired
    private CitizenTaxStatusRepository citizenTaxStatusRepository;



    public List<CitizenTaxStatus> findAllTaxStatus() {

        return citizenTaxStatusRepository.findAll();
    }




    public CitizenTaxStatus findTaxStatusByCitizen(String citizenID) throws CitizenNotFoundException{

        CitizenTaxStatus taxStatus = null;


        Optional<CitizenTaxStatus> optTaxStatus = citizenTaxStatusRepository.findByCitizenID(citizenID);
        if(!optTaxStatus.isPresent())
            throw new CitizenNotFoundException();

        taxStatus = optTaxStatus.get();

        return taxStatus;
    }

}
