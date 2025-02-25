package com.modsen.payment_service.repositories;

import com.modsen.payment_service.models.enitties.PassengerBankAccount;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PassengerBankAccountRepository extends MongoRepository<PassengerBankAccount, String> {
    Optional<PassengerBankAccount> findByPassengerId(String passengerId);
}
