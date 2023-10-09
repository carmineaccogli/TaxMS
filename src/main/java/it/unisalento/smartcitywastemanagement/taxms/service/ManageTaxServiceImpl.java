package it.unisalento.smartcitywastemanagement.taxms.service;


import it.unisalento.smartcitywastemanagement.taxms.domain.CitizenTaxStatus;
import it.unisalento.smartcitywastemanagement.taxms.domain.Tax;
import it.unisalento.smartcitywastemanagement.taxms.domain.TaxRate;
import it.unisalento.smartcitywastemanagement.taxms.dto.CitizenWasteMetricsDTO;
import it.unisalento.smartcitywastemanagement.taxms.dto.GeneratedVolumePerYearDTO;
import it.unisalento.smartcitywastemanagement.taxms.exceptions.AnnualTaxAlreadyEmittedException;
import it.unisalento.smartcitywastemanagement.taxms.exceptions.TaxNotFoundException;
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
     *      1.1 Se Sì, si avvisa che la richiesta non è stata eseguita
     * 2 Contatto DisposalMS per ottenere i dati dei volumi generati dai cittadini nell'anno corrente
     * 3 Contatto CitizenMS per ottenere la lista di tutti i cittadini presenti
     * 4 Calcolo la data di scadenza pari a 6 mesi a partire dall'emissione
     * 5 Ottengo il numero di tasse presenti per poter generare il numero progressivo che farà parte del codice di ogni tassa
     * 6 Per ogni citizen presente nella lista di ID ottenuta, prelevo la tassa base da taxRates
     * 7 Controllo che il citizen in questione abbia effettuato o meno conferimenti
     *      7.1 Se sì, calcolo la tassa annuale moltiplicando ogni campo per il rispettivo taxRate e sommandoci la tassa base
     *      7.2 Altrimenti l'ammontare della tassa corrisponderà alla sola tassa base
     * 8 Creo l'oggetto tassa per il singolo cittadino valido per l'anno corrente
     * 9 Salvo l'oggetto tassa creato
     * 10 Aggiorno il corrispettivo taxStatus a false(non regolare)
     *      9.1 Se esiste un documento di taxStatus per il cittadino, si aggiorna a false
     *      9.2 Altrimenti si crea e si setta a false
     */


    public List<String> emitTaxes(Map<String,Double> taxRates) throws AnnualTaxAlreadyEmittedException, TaxRateNotFoundException{

        // 1
        boolean alreadyEmitted = checkAlreadyEmitted(Year.now().getValue() -1);

        // 1.1
        if(alreadyEmitted)
            throw new AnnualTaxAlreadyEmittedException();

        // 2
        String[] allCitizenIDs = apiService.APICALL_getCitizenData().block();

        // 3
        List<CitizenWasteMetricsDTO> disposalData = apiService.APICALL_getDisposalData(Year.now().getValue() - 1).block(); //inserire -1


        //List<TaxRate> taxRates = taxRateService.findAllTaxRates();

        // 4
        LocalDate expireDate = getExpireDate();

        // 5
        int p = taxRepository.findAll().size() + 1;
        List<String> createdTaxes = new ArrayList<>();


        for(String citizenID: allCitizenIDs) {


            BigDecimal totalTax = BigDecimal.ZERO;

            // 6
            BigDecimal fixedFee = getFeeMultiplierByType("FixedFee",taxRates);

            // 7
            CitizenWasteMetricsDTO citizenMetric = findCitizenWasteMetricWithID(disposalData,citizenID);

            // 7.1
            if(citizenMetric != null) {
                GeneratedVolumePerYearDTO currentVolume = citizenMetric.getYearlyVolumes().get(0);
                BigDecimal taxAmount = calculateTax(currentVolume, taxRates);
                totalTax = fixedFee.add(taxAmount);
            }
            else {
                // 7.2
                totalTax = fixedFee;
            }

            // 8
            Tax newYearTax = new Tax();
            newYearTax.setCitizenID(citizenID);
            newYearTax.setYear(Year.now().getValue() - 1);
            newYearTax.setAmount(totalTax.floatValue());
            newYearTax.setExpireDate(expireDate);
            newYearTax.setTaxCode(generateTaxCode(p));

            // 8
            Tax tax = taxRepository.save(newYearTax);
            createdTaxes.add(tax.getTaxCode());
            p++;

            // 9
            updateCitizenTaxStatus(citizenID);
        }

        return createdTaxes;
    }


    public Tax findTaxByID(String taxID) throws TaxNotFoundException {

        Optional<Tax> optTax = taxRepository.findById(taxID);
        if(!optTax.isPresent())
            throw new TaxNotFoundException();

        return optTax.get();
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

    public boolean checkTaxToPay(String citizenID) {
        return taxRepository.existsByCitizenIDAndPaymentDate(citizenID, null);
    }



    public boolean checkAlreadyEmitted(int year) {
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


    private CitizenWasteMetricsDTO findCitizenWasteMetricWithID(List<CitizenWasteMetricsDTO> citizenWasteMetricsDTOList, String citizenID) {
        for (CitizenWasteMetricsDTO metrics : citizenWasteMetricsDTOList) {
            if (metrics.getCitizenID().equals(citizenID)) {
                return metrics;
            }
        }
        return null;
    }
}
