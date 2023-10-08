package it.unisalento.smartcitywastemanagement.taxms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.Date;

public class TaxDTO {


    private String id;

    private String citizenID;

    private Float amount;

    private int year;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expireDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate paymentDate;


    private String taxCode;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCitizenID() {
        return citizenID;
    }

    public void setCitizenID(String citizenID) {
        this.citizenID = citizenID;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public LocalDate getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }


    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }
}
