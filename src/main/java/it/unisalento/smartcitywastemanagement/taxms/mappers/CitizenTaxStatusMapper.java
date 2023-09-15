package it.unisalento.smartcitywastemanagement.taxms.mappers;


import it.unisalento.smartcitywastemanagement.taxms.domain.CitizenTaxStatus;
import it.unisalento.smartcitywastemanagement.taxms.dto.CitizenTaxStatusDTO;
import org.springframework.stereotype.Component;

@Component
public class CitizenTaxStatusMapper {


    public CitizenTaxStatusDTO toTaxStatusDTO(CitizenTaxStatus taxStatus) {

        CitizenTaxStatusDTO taxStatusDTO = new CitizenTaxStatusDTO();

        taxStatusDTO.setId(taxStatus.getId());
        taxStatusDTO.setCitizenID(taxStatus.getCitizenID());
        taxStatusDTO.setTaxStatus(taxStatus.isTaxStatus());

        return taxStatusDTO;
    }
}
