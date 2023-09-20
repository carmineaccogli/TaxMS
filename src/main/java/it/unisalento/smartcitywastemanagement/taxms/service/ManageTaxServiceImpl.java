package it.unisalento.smartcitywastemanagement.taxms.service;


import it.unisalento.smartcitywastemanagement.taxms.domain.CitizenTaxStatus;
import it.unisalento.smartcitywastemanagement.taxms.domain.Tax;
import it.unisalento.smartcitywastemanagement.taxms.domain.TaxRate;
import it.unisalento.smartcitywastemanagement.taxms.dto.CitizenWasteMetricsDTO;
import it.unisalento.smartcitywastemanagement.taxms.dto.GeneratedVolumePerYearDTO;
import it.unisalento.smartcitywastemanagement.taxms.exceptions.AnnualTaxAlreadyEmittedException;
import it.unisalento.smartcitywastemanagement.taxms.exceptions.TaxRateNotFoundException;
import it.unisalento.smartcitywastemanagement.taxms.repositories.CitizenTaxStatusRepository;
import it.unisalento.smartcitywastemanagement.taxms.repositories.TaxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ManageTaxServiceImpl implements ManageTaxService{


    @Autowired
    TaxRepository taxRepository;

    @Autowired
    ApiService apiService;

    @Autowired
    TaxRateService taxRateService;

    @Autowired
    CitizenTaxStatusRepository citizenTaxStatusRepository;


    public List<Tax> findTaxesOfCitizen(String citizenID)  {

        return taxRepository.findByCitizenIDOrderByYear(citizenID);
    }



    /** FUNZIONE EMISSIONE TASSE ANNUALI
     *
     * 1 Controllo se le tasse per l'anno corrente sono state già emesse
     *      1.1 Se Sì, si avvisa il frontend che la richiesta non è stata eseguita
     * 2 Contatto DisposalMS per ottenere i dati dei volumi generati dai cittadini nell'anno corrente
     * 3 Ottengo i taxRate attuali
     * 4 Calcolo la data di scadenza pari a 6 mesi a partire dall'emissione
     * 5 Ottengo il numero di tasse presenti per poter generare il numero progressivo che farà parte del codice di ogni tassa
     * 6 Per ogni elemento di disposalData calcolo la tassa annuale moltiplicando ogni campo per il rispettivo taxRate
     * 7 Creo l'oggetto tassa per il singolo cittadino valido per l'anno corrente
     * 8 Salvo l'oggetto tassa creato
     * 9 Aggiorno il corrispettivo taxStatus a false(non regolare)
     *      9.1 Se esiste un documento di taxStatus per il cittadino, si aggiorna a false
     *      9.2 Altrimenti si crea e si setta a false
     */


    public List<String> emitTaxes(Map<String,Double> taxRates) throws AnnualTaxAlreadyEmittedException, TaxRateNotFoundException{

        // 1
        boolean alreadyEmitted = checkAlreadyEmitted(Year.now().getValue());

        // 1.1
        if(alreadyEmitted)
            throw new AnnualTaxAlreadyEmittedException();

        System.out.println("TESTO LA CHIAMATA API");
        // 2
        List<CitizenWasteMetricsDTO> disposalData = apiService.APICALL_getDisposalData(Year.now().getValue()).block();

        // 3
        //List<TaxRate> taxRates = taxRateService.findAllTaxRates();


        // 4
        LocalDate expireDate = getExpireDate();

        // 5
        int p = taxRepository.findAll().size() + 1;
        List<String> createdTaxes = new ArrayList<>();

        for(CitizenWasteMetricsDTO wasteMetricsDTO: disposalData) {
            System.out.println("SONO NEL FOR");

            // 6
            GeneratedVolumePerYearDTO currentVolume = wasteMetricsDTO.getYearlyVolumes().get(0);
            BigDecimal taxAmount = calculateTax(currentVolume, taxRates);

            // 7
            Tax newYearTax = new Tax();
            newYearTax.setCitizenID(wasteMetricsDTO.getCitizenID());
            newYearTax.setYear(Year.now().getValue());
            newYearTax.setAmount(taxAmount.floatValue());
            newYearTax.setExpireDate(expireDate);
            newYearTax.setTaxCode(generateTaxCode(p));

            // 8
            Tax tax = taxRepository.save(newYearTax);
            createdTaxes.add(tax.getTaxCode());
            p++;

            // 9
            updateCitizenTaxStatus(wasteMetricsDTO.getCitizenID());
        }

        return createdTaxes;
    }





    private String generateTaxCode(int progressiveNumber) {
        return String.format("TX-0%d", progressiveNumber);
    }



    private void updateCitizenTaxStatus(String citizenID) {
        Optional<CitizenTaxStatus> optCitizenStatus = citizenTaxStatusRepository.findByCitizenID(citizenID);

        if(!optCitizenStatus.isPresent()) {
            CitizenTaxStatus citizenTaxStatus = new CitizenTaxStatus();
            citizenTaxStatus.setCitizenID(citizenID);
            citizenTaxStatus.setTaxStatus(false);
            citizenTaxStatusRepository.save(citizenTaxStatus);

        }else {
            optCitizenStatus.get().setTaxStatus(false);
            citizenTaxStatusRepository.save(optCitizenStatus.get());

        }

    }

    private boolean checkAlreadyEmitted(int year) {
        return taxRepository.existsByYear(year);
    }


    /*private BigDecimal getFeeMultiplierByType(String type, List<TaxRate> taxRates) throws TaxRateNotFoundException {

        Optional<BigDecimal> feeMultiplier = taxRates.stream()
                .filter(item -> item.getType().equals(type))
                .map(TaxRate::getFeeMultiplier)
                .findFirst();

        if(!feeMultiplier.isPresent())
            throw new TaxRateNotFoundException(type);

        return feeMultiplier.get();
    }*/

    private BigDecimal getFeeMultiplierByType(String type, Map<String,Double> taxRates) throws TaxRateNotFoundException {

        if(taxRates.containsKey(type))
            return BigDecimal.valueOf(taxRates.get(type));
        else
            throw new TaxRateNotFoundException(type);
    }


    private BigDecimal calculateTax(GeneratedVolumePerYearDTO currentVolume, Map<String,Double> taxRates) throws TaxRateNotFoundException {
        BigDecimal mixedWaste_taxAmount = getFeeMultiplierByType("Indifferenziata",taxRates).multiply(currentVolume.getMixedWaste());
        BigDecimal sortedWaste_taxAmount = BigDecimal.ZERO;

        for (Map.Entry<String, BigDecimal> entry : currentVolume.getSortedWaste().entrySet()) {

            BigDecimal amount = getFeeMultiplierByType(entry.getKey(),taxRates).multiply(entry.getValue());
            sortedWaste_taxAmount = sortedWaste_taxAmount.add(amount);
        }

        return mixedWaste_taxAmount.add(sortedWaste_taxAmount);
    }


    private LocalDate getExpireDate() {
        LocalDate todayDate = LocalDate.now();
        return todayDate.plusMonths(6);
    }
}
