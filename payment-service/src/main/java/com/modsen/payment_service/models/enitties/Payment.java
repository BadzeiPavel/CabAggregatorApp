package com.modsen.payment_service.models.enitties;

import com.modsen.payment_service.enums.PaymentStatus;
import com.modsen.payment_service.models.RideInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Document(collection = "payment")
public class Payment {
    @Id
    private String id;
    private String rideId;
    private String driverId;
    private String passengerId;
    private BigDecimal cost;
    private PaymentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private String promoCode;
    private RideInfo rideInfo;
}
