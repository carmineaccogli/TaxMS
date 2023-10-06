package it.unisalento.smartcitywastemanagement.taxms.restcontrollers;


import it.unisalento.smartcitywastemanagement.taxms.domain.CitizenTaxStatus;
import it.unisalento.smartcitywastemanagement.taxms.dto.CitizenTaxStatusDTO;
import it.unisalento.smartcitywastemanagement.taxms.exceptions.CitizenNotFoundException;
import it.unisalento.smartcitywastemanagement.taxms.mappers.CitizenTaxStatusMapper;
import it.unisalento.smartcitywastemanagement.taxms.service.CitizenTaxStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(value="/api/taxStatus")
public class CitizenTaxStatusRestController {

    @Autowired
    private CitizenTaxStatusService citizenTaxStatusService;


    @Autowired
    private CitizenTaxStatusMapper citizenTaxStatusMapper;


    @RequestMapping(value="/", method= RequestMethod.GET)
    public ResponseEntity<List<CitizenTaxStatusDTO>> getAllTaxStatus() {

        List<CitizenTaxStatus> results = citizenTaxStatusService.findAllTaxStatus();

        List<CitizenTaxStatusDTO> all_taxStatus = fromTaxStatusToDTOArray(results);

        if(all_taxStatus.isEmpty())
            return ResponseEntity.noContent().build();


        return ResponseEntity.ok(all_taxStatus);
    }

    @RequestMapping(value="/{citizenID}", method = RequestMethod.GET)
    public ResponseEntity<CitizenTaxStatusDTO> getTaxStatusByCitizen(@PathVariable String citizenID)  {

        CitizenTaxStatus result = citizenTaxStatusService.findTaxStatusByCitizen(citizenID);

        if(result == null)
            return ResponseEntity.noContent().build();
        else {
            CitizenTaxStatusDTO citizenTaxStatusDTO = citizenTaxStatusMapper.toTaxStatusDTO(result);
            return ResponseEntity.ok(citizenTaxStatusDTO);
        }
    }




    private List<CitizenTaxStatusDTO> fromTaxStatusToDTOArray(List<CitizenTaxStatus> entityTaxStatus) {

        List<CitizenTaxStatusDTO> result = new ArrayList<>();

        for(CitizenTaxStatus taxStatus: entityTaxStatus) {
            CitizenTaxStatusDTO taxStatusDTO = citizenTaxStatusMapper.toTaxStatusDTO(taxStatus);
            result.add(taxStatusDTO);
        }
        return result;

    }
}
