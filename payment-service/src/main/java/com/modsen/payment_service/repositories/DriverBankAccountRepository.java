package com.modsen.payment_service.repositories;

import com.modsen.payment_service.models.enitties.DriverBankAccount;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverBankAccountRepository extends MongoRepository<DriverBankAccount, String> {
    Optional<DriverBankAccount> findByDriverId(String driverId);
}
