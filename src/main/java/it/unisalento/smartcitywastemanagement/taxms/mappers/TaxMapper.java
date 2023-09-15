package it.unisalento.smartcitywastemanagement.taxms.mappers;


import it.unisalento.smartcitywastemanagement.taxms.domain.Tax;
import it.unisalento.smartcitywastemanagement.taxms.dto.TaxDTO;
import org.springframework.stereotype.Component;

@Component
public class TaxMapper {


    public TaxDTO toTaxDTO(Tax tax) {
        TaxDTO taxDTO = new TaxDTO();

        taxDTO.setId(tax.getId());
        taxDTO.setAmount(tax.getAmount());
        taxDTO.setCitizenID(tax.getCitizenID());
        taxDTO.setYear(tax.getYear());
        taxDTO.setExpireDate(tax.getExpireDate());
        taxDTO.setPaymentDate(tax.getPaymentDate());
        taxDTO.setTaxCode(tax.getTaxCode());


        return taxDTO;
    }
}
