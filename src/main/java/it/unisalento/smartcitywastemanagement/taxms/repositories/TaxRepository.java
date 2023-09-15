package it.unisalento.smartcitywastemanagement.taxms.repositories;

import it.unisalento.smartcitywastemanagement.taxms.domain.Tax;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TaxRepository extends MongoRepository<Tax, String> {


    List<Tax> findByCitizenIDOrderByYear(String citizenID);

    boolean existsByYear(int year);

    Optional<Tax> findById(String taxID);


}
