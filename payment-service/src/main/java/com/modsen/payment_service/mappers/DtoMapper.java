package com.modsen.payment_service.mappers;

import com.modsen.payment_service.models.dtos.DriverBankAccountDTO;
import models.dtos.PassengerBankAccountDTO;
import models.dtos.PaymentDTO;
import com.modsen.payment_service.models.enitties.DriverBankAccount;
import com.modsen.payment_service.models.enitties.PassengerBankAccount;
import com.modsen.payment_service.models.enitties.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.stereotype.Service;

@Service
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DtoMapper {

    Payment toPayment(PaymentDTO paymentDTO);

    DriverBankAccount toDriverbankAccount(DriverBankAccountDTO driverBankAccountDTO);

    PassengerBankAccount toPassengerBankAccount(PassengerBankAccountDTO passengerBankAccountDTO);
}
