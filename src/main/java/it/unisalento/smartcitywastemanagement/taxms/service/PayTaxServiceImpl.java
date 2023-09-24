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
    private CitizenTaxStatusService citizenTaxStatusService;

    @Autowired
    private StripePaymentService stripePaymentService;

    @Autowired
    private ManageTaxService manageTaxService;

    /** FUNZIONE PER PERMETTERE IL PAGAMENTO DI UNA TASSA
     *
     * 1 Ricerca tassa per Id e controllo status
     *      1.1 Se la tassa non esiste, eccezione
     *      1.2 Se esiste ma risulta già pagata, eccezione
     * 2 Check sull'esistenza del customer per email
     *      2.1 Se non esiste, si crea il customer e si aggiunge la carta indicata
     *      2.2 Altrimenti, facciamo il controllo se la carta risulta essere presente o meno
     * 3 Check status pagamento
     *      3.1 Se lo stato è "successful", si aggiorna il campo paymentDate della tassa per indicare l'avvenuto pagamento
     * 4 Check per capire se il cittadino ha ulteriori tasse da pagare
     *      4.1 Se no, aggiorniamo il suo status indicando una situazione regolare
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
        Customer existsCustomer = stripePaymentService.getCustomerByEmail(paymentInfo.getEmail());
        Charge paymentResponse;

        // 2.1
        if(existsCustomer == null) {
            Customer newCustomer = stripePaymentService.createCustomer(paymentInfo.getCardToken(), paymentInfo.getEmail(), paymentInfo.getFullName());
            paymentResponse = stripePaymentService.chargeCustomerDefaultCard(newCustomer.getId(), paymentInfo.getAmount());
        }
        // 2.2
        else {
            paymentResponse = stripePaymentService.chargeCustomerCard(existsCustomer.getId(), paymentInfo.getAmount(), paymentInfo.getCardToken());
        }


        // 3
        if(paymentResponse.getStatus().equals("succeeded")) {
            // 3.1
            taxToPay.setPaymentDate(getPaymentDate(paymentResponse.getCreated()));
            taxRepository.save(taxToPay);
        }

        // 4
        boolean existsTaxToPay = manageTaxService.checkTaxToPay(taxToPay.getCitizenID());
        // 4.1
        if(!existsTaxToPay) {
            citizenTaxStatusService.updateCitizenTaxStatus(taxToPay.getCitizenID(), true);
        }


        return paymentResponse;
    }



    private LocalDate getPaymentDate(long timestamp) {

        Instant instant = Instant.ofEpochSecond(timestamp);
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }




    private Tax checkTaxExists(String taxID) {

        Optional<Tax> optionalTax = taxRepository.findById(taxID);
        if(!optionalTax.isPresent())
            return null;

        return optionalTax.get();
    }
}
