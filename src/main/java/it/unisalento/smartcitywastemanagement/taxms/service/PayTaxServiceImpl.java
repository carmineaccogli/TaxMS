package it.unisalento.smartcitywastemanagement.taxms.service;


import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import it.unisalento.smartcitywastemanagement.taxms.domain.CitizenTaxStatus;
import it.unisalento.smartcitywastemanagement.taxms.domain.PaymentInfo;
import it.unisalento.smartcitywastemanagement.taxms.domain.Tax;
import it.unisalento.smartcitywastemanagement.taxms.exceptions.CitizenNotFoundException;
import it.unisalento.smartcitywastemanagement.taxms.exceptions.TaxAlreadyPaidException;
import it.unisalento.smartcitywastemanagement.taxms.exceptions.TaxNotFoundException;
import it.unisalento.smartcitywastemanagement.taxms.repositories.TaxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
public class PayTaxServiceImpl implements PayTaxService{


    @Autowired
    private TaxRepository taxRepository;

    @Autowired
    private StripePaymentService stripePaymentService;

    /** FUNZIONE PER PERMETTERE IL PAGAMENTO DI UNA TASSA
     *
     * 1 Ricerca tassa per Id e controllo status
     *      1.1 Se la tassa non esiste, eccezione
     *      1.2 Se esiste ma risulta già pagata, eccezione
     * 2 Creo il cliente Stripe per le info fornite e contatto per eseguire il pagamento
     * 3 Check status pagamento
     *      3.1 Se lo stato è "successful", si aggiorna il campo paymentDate della tassa
     *      3.2 Altrimenti si restituisce un errore nella risposta
     */

    public Charge payTax(PaymentInfo paymentInfo, String taxID) throws Exception {

        // 1
        Tax taxToPay = checkTaxExists(taxID);

        // 1.1
        if(taxToPay == null)
            throw new TaxNotFoundException();

        // 1.2
        if(taxToPay.getPaymentDate() != null)
            throw new TaxAlreadyPaidException();

        // 2
        Customer client = stripePaymentService.createCustomer(paymentInfo.getCardToken(), paymentInfo.getEmail(), paymentInfo.getFullName());
        Charge paymentResponse = stripePaymentService.chargeCustomerCard(client.getId(),paymentInfo.getAmount());

        // 3
        if(paymentResponse.getStatus().equals("succeeded")) {
            taxToPay.setPaymentDate(getPaymentDate(paymentResponse.getCreated()));
            taxRepository.save(taxToPay);
        }

        // 4
        return paymentResponse;
    }



    private LocalDate getPaymentDate(long timestamp) {

        Instant instant = Instant.ofEpochMilli(timestamp);
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();

        // Estrai la data da LocalDateTime
        return localDateTime.toLocalDate();
    }




    private Tax checkTaxExists(String taxID) {

        Optional<Tax> optionalTax = taxRepository.findById(taxID);
        if(!optionalTax.isPresent())
            return null;

        return optionalTax.get();
    }
}
