package it.unisalento.smartcitywastemanagement.taxms.dto;


import java.util.List;

public class CitizenWasteMetricsDTO {

    private String id;
    private String citizenID;
    private List<GeneratedVolumePerYearDTO> yearlyVolumes;


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

    public List<GeneratedVolumePerYearDTO> getYearlyVolumes() {
        return yearlyVolumes;
    }

    public void setYearlyVolumes(List<GeneratedVolumePerYearDTO> yearlyVolumes) {
        this.yearlyVolumes = yearlyVolumes;
    }
}
