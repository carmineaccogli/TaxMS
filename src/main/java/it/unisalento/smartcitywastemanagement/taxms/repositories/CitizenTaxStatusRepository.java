package it.unisalento.smartcitywastemanagement.taxms.repositories;

import it.unisalento.smartcitywastemanagement.taxms.domain.CitizenTaxStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CitizenTaxStatusRepository extends MongoRepository<CitizenTaxStatus, String> {

    List<CitizenTaxStatus> findAll();

    Optional<CitizenTaxStatus> findByCitizenID(String citizenID);

}
