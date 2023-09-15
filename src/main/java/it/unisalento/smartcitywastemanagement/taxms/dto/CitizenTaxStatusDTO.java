package it.unisalento.smartcitywastemanagement.taxms.dto;

public class CitizenTaxStatusDTO {


    private String Id;

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
