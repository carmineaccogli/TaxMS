package it.unisalento.smartcitywastemanagement.taxms.restcontrollers;


import it.unisalento.smartcitywastemanagement.taxms.domain.Tax;
import it.unisalento.smartcitywastemanagement.taxms.dto.PaymentRequestDTO;
import it.unisalento.smartcitywastemanagement.taxms.dto.ResponseDTO;
import it.unisalento.smartcitywastemanagement.taxms.dto.TaxDTO;
import it.unisalento.smartcitywastemanagement.taxms.exceptions.AnnualTaxAlreadyEmittedException;
import it.unisalento.smartcitywastemanagement.taxms.exceptions.CitizenNotFoundException;
import it.unisalento.smartcitywastemanagement.taxms.exceptions.TaxRateNotFoundException;
import it.unisalento.smartcitywastemanagement.taxms.mappers.TaxMapper;
import it.unisalento.smartcitywastemanagement.taxms.service.ManageTaxService;
import it.unisalento.smartcitywastemanagement.taxms.service.PayTaxService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value="/api/tax")
public class TaxRestController {

    @Autowired
    TaxMapper taxMapper;

    @Autowired
    ManageTaxService manageTaxService;

    @Autowired
    PayTaxService payTaxService;

    @RequestMapping(value="/emit", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> requestEmitTaxes() throws AnnualTaxAlreadyEmittedException, TaxRateNotFoundException {

        List<String> generatedTaxCodes = manageTaxService.emitTaxes();

        return new ResponseEntity<>(
                new ResponseDTO("Emitted "+generatedTaxCodes.size()+" new Taxes",generatedTaxCodes),
                HttpStatus.CREATED);
    }


    @RequestMapping(value="/citizen/{citizenID}", method = RequestMethod.GET)
    public ResponseEntity<List<TaxDTO>> getTaxesByCitizen(@PathVariable String citizenID) throws CitizenNotFoundException {

        List<Tax> results = manageTaxService.findTaxesOfCitizen(citizenID);

        List<TaxDTO> all_taxes = fromTaxToDTOArray(results);

        if(all_taxes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(all_taxes);
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
