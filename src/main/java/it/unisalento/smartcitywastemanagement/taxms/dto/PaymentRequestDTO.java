package it.unisalento.smartcitywastemanagement.taxms.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PaymentRequestDTO {


    @NotNull(message="{NotNull.PaymentRequest.email}")
    @Email
    private String email;

    @NotBlank(message="{NotBlank.PaymentRequest.fullName}")
    private String fullName;

    @NotNull(message = "{NotNull.PaymentRequest.amount}")
    private Double amount;

    @NotBlank(message= "{NotBlank.PaymentRequest.cardToken}")
    private String cardToken;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCardToken() {
        return cardToken;
    }

    public void setCardToken(String cardToken) {
        this.cardToken = cardToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
