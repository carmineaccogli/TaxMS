package it.unisalento.smartcitywastemanagement.taxms.restcontrollers;


import it.unisalento.smartcitywastemanagement.taxms.domain.Tax;
import it.unisalento.smartcitywastemanagement.taxms.dto.ResponseDTO;
import it.unisalento.smartcitywastemanagement.taxms.dto.TaxDTO;
import it.unisalento.smartcitywastemanagement.taxms.exceptions.AnnualTaxAlreadyEmittedException;
import it.unisalento.smartcitywastemanagement.taxms.exceptions.CitizenNotFoundException;
import it.unisalento.smartcitywastemanagement.taxms.exceptions.TaxNotFoundException;
import it.unisalento.smartcitywastemanagement.taxms.exceptions.TaxRateNotFoundException;
import it.unisalento.smartcitywastemanagement.taxms.mappers.TaxMapper;
import it.unisalento.smartcitywastemanagement.taxms.service.ManageTaxService;
import it.unisalento.smartcitywastemanagement.taxms.service.PayTaxService;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping(value="/api/tax")
@Validated
public class TaxRestController {

    @Autowired
    TaxMapper taxMapper;

    @Autowired
    ManageTaxService manageTaxService;

    @Autowired
    PayTaxService payTaxService;

    @PreAuthorize("hasRole('ROLE_MunicipalOffice')")
    @RequestMapping(value="/emit", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> requestEmitTaxes(@RequestBody  Map<@NotBlank String, @NotNull @DecimalMin(value = "0.0", inclusive = false) Double> feeMultiplierByType) throws AnnualTaxAlreadyEmittedException, TaxRateNotFoundException {

        List<String> generatedTaxCodes = manageTaxService.emitTaxes(feeMultiplierByType);

        return new ResponseEntity<>(
                new ResponseDTO("Emitted "+generatedTaxCodes.size()+" new Taxes",generatedTaxCodes),
                HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ROLE_MunicipalOffice')")
    @RequestMapping(value="/taxStatus", method= RequestMethod.GET)
    public ResponseEntity<ResponseDTO> getTaxStatusCurrentYear() {

        boolean taxStatus = manageTaxService.checkAlreadyEmitted(Year.now().getValue() - 1);
        return new ResponseEntity<>(
                new ResponseDTO( "Tax issued", taxStatus),
                HttpStatus.OK);
    }


    @PreAuthorize("hasAnyRole('ROLE_MunicipalOffice','ROLE_Citizen')")
    @RequestMapping(value="/citizen/{citizenID}", method = RequestMethod.GET)
    public ResponseEntity<List<TaxDTO>> getTaxesByCitizen(@PathVariable String citizenID) throws CitizenNotFoundException {

        List<Tax> results = manageTaxService.findTaxesOfCitizen(citizenID);

        List<TaxDTO> all_taxes = fromTaxToDTOArray(results);

        if(all_taxes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(all_taxes);
    }

    @PreAuthorize("hasAnyRole('ROLE_MunicipalOffice','ROLE_Citizen')")
    @RequestMapping(value="/{taxID}", method = RequestMethod.GET)
    public ResponseEntity<TaxDTO> getTaxByID(@PathVariable("taxID") String taxID) throws TaxNotFoundException {

        Tax tax = manageTaxService.findTaxByID(taxID);
        TaxDTO taxDTO = taxMapper.toTaxDTO(tax);

        return ResponseEntity.ok(taxDTO);
    }







    private List<TaxDTO> fromTaxToDTOArray(List<Tax> entityTax) {
        List<TaxDTO> result = new ArrayList<>();

        for(Tax tax: entityTax) {
            TaxDTO taxDTO = taxMapper.toTaxDTO(tax);
            result.add(taxDTO);
        }
        return result;
    }



}
