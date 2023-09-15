package it.unisalento.smartcitywastemanagement.taxms.domain;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document("taxRate")
public class TaxRate {

    @Id
    private String Id;

    private String type;

    private BigDecimal feeMultiplier;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getFeeMultiplier() {
        return feeMultiplier;
    }

    public void setFeeMultiplier(BigDecimal feeMultiplier) {
        this.feeMultiplier = feeMultiplier;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
}

