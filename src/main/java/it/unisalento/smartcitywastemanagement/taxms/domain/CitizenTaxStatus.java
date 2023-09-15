package it.unisalento.smartcitywastemanagement.taxms.domain;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("citizenTaxStatus")
public class CitizenTaxStatus {

    @Id
    private String Id;

    @Indexed(unique = true)
    private String citizenID;

    private boolean taxStatus;


    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getCitizenID() {
        return citizenID;
    }

    public void setCitizenID(String citizenID) {
        this.citizenID = citizenID;
    }

    public boolean isTaxStatus() {
        return taxStatus;
    }

    public void setTaxStatus(boolean taxStatus) {
        this.taxStatus = taxStatus;
    }
}
