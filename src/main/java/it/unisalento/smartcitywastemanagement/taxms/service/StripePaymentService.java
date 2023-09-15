package it.unisalento.smartcitywastemanagement.taxms.service;

import com.stripe.model.Charge;
import com.stripe.model.Customer;

public interface StripePaymentService {


    Charge chargeNewCard(String token, double amount) throws Exception;

    Charge chargeCustomerCard(String customerId, double amount) throws Exception;

    Customer createCustomer(String token, String email, String fullName) throws Exception;
}
