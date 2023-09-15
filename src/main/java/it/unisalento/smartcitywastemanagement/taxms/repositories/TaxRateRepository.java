package it.unisalento.smartcitywastemanagement.taxms.repositories;

import it.unisalento.smartcitywastemanagement.taxms.domain.Tax;
import it.unisalento.smartcitywastemanagement.taxms.domain.TaxRate;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TaxRateRepository extends MongoRepository<TaxRate, String> {


    List<TaxRate> findAll();
}
