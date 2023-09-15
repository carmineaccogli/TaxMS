package it.unisalento.smartcitywastemanagement.taxms.service;

import com.stripe.model.Charge;
import it.unisalento.smartcitywastemanagement.taxms.domain.PaymentInfo;

public interface PayTaxService {


    Charge payTax(PaymentInfo paymentInfo, String taxID) throws Exception;
}
