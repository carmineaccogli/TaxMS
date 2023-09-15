package it.unisalento.smartcitywastemanagement.taxms.dto;

import com.fasterxml.jackson.databind.DatabindException;
import com.stripe.model.Charge;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class PaymentResponseDTO {


    private String paymentID;

    private String status;

    private double amount;

    private Date paymentDate;

    private String currency;


    public String getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(String paymentID) {
        this.paymentID = paymentID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }



    public PaymentResponseDTO(Charge stripeCharge) {
        this.paymentID = stripeCharge.getId();
        this.amount = stripeCharge.getAmount() / 100.0;
        this.currency = stripeCharge.getCurrency();
        this.status = stripeCharge.getStatus();
        this.paymentDate = new Date(stripeCharge.getCreated() * 1000);
    }
}
