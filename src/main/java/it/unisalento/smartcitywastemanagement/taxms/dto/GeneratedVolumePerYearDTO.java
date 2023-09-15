package it.unisalento.smartcitywastemanagement.taxms.dto;


import java.math.BigDecimal;
import java.util.Map;

public class GeneratedVolumePerYearDTO {


    private int year;

    private BigDecimal mixedWaste;

    private Map<String, BigDecimal> sortedWaste;


    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public BigDecimal getMixedWaste() {
        return mixedWaste;
    }

    public void setMixedWaste(BigDecimal mixedWaste) {
        this.mixedWaste = mixedWaste;
    }

    public Map<String, BigDecimal> getSortedWaste() {
        return sortedWaste;
    }

    public void setSortedWaste(Map<String, BigDecimal> sortedWaste) {
        this.sortedWaste = sortedWaste;
    }
}
