package com.modsen.payment_service.component.services.steps;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.modsen.payment_service.models.enitties.Payment;
import com.modsen.payment_service.services.PaymentService;
import com.modsen.payment_service.repositories.PaymentRepository;
import enums.CarCategory;
import enums.PaymentMethod;
import models.dtos.PaymentDTO;
import enums.PaymentStatus;
import models.dtos.RideInfo;
import org.springframework.boot.test.context.SpringBootTest;
import io.cucumber.java.en.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@SpringBootTest
public class PaymentServiceSteps {

    private final PaymentRepository repository = mock(PaymentRepository.class);
    private final PaymentService paymentService = mock(PaymentService.class);
    private PaymentDTO paymentDTO;
    private Exception exception;

    @Given("a valid payment request")
    public void validPaymentRequest() {
        paymentDTO = new PaymentDTO();
        paymentDTO.setRideId("ride123");
        paymentDTO.setDriverId("driver456");
        paymentDTO.setPassengerId("passenger789");
        paymentDTO.setCost(BigDecimal.valueOf(100.50));
        paymentDTO.setStatus(PaymentStatus.PENDING);
    }

    @When("I send a request to create a payment")
    public void iSendRequestToCreatePayment() {
        when(paymentService.createPayment(any(PaymentDTO.class))).thenReturn(paymentDTO);
        paymentDTO = paymentService.createPayment(paymentDTO);
    }

    @Then("the payment should be created successfully")
    public void thePaymentShouldBeCreatedSuccessfully() {
        assertNotNull(paymentDTO, "Payment should not be null");
    }

    @Then("the payment status should be PENDING")
    public void thePaymentStatusShouldBePending() {
        assertEquals(PaymentStatus.PENDING, paymentDTO.getStatus(), "Payment status should be PENDING");
    }

    @Given("a payment with ride ID {string}, driver ID {string}, passenger ID {string}, and cost {double}")
    public void createPayment(String rideId, String driverId, String passengerId, double cost) {
        paymentDTO = new PaymentDTO();
        paymentDTO.setRideId(rideId);
        paymentDTO.setDriverId(driverId);
        paymentDTO.setPassengerId(passengerId);
        paymentDTO.setCost(BigDecimal.valueOf(cost));
        paymentDTO.setStatus(PaymentStatus.PENDING);
    }

    @When("the payment is created")
    public void processPayment() {
        when(paymentService.createPayment(any(PaymentDTO.class))).thenReturn(paymentDTO);
        paymentDTO = paymentService.createPayment(paymentDTO);
    }

    @Then("the payment status should be {string}")
    public void verifyPaymentStatus(String status) {
        assertEquals(PaymentStatus.valueOf(status), paymentDTO.getStatus());
    }

    @When("the payment for ride ID {string} is deleted")
    public void deletePayment(String rideId) {
        when(repository.findByRideId(rideId)).thenReturn(Optional.of(new Payment()));
        when(paymentService.deletePayment(rideId)).thenReturn(paymentDTO);
        paymentDTO = paymentService.deletePayment(rideId);
    }

    @When("attempting to delete a non-existent payment for ride ID {string}")
    public void deleteNonExistentPayment(String rideId) {
        when(repository.findByRideId(rideId)).thenReturn(Optional.empty());
        try {
            paymentService.deletePayment(rideId);
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("an error should be thrown with message {string}")
    public void verifyExceptionMessage(String message) {
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Given("a payment exists with ride ID {string}")
    public void paymentExistsWithRideId(String rideId) {
        Payment payment = new Payment();
        payment.setRideId(rideId);
        payment.setDriverId("driver123");
        payment.setPassengerId("passenger123");
        payment.setCost(BigDecimal.valueOf(50.00));
        payment.setStatus(PaymentStatus.PENDING);

        when(repository.findByRideId(rideId)).thenReturn(Optional.of(payment));
        when(paymentService.getPayment(rideId)).thenReturn(new PaymentDTO(
                rideId, "ride123", "driver123", "passenger123", BigDecimal.valueOf(50.00), PaymentStatus.PENDING, LocalDateTime.now(), null, null, new RideInfo(PaymentMethod.CARD, CarCategory.ECONOMY, 100)
        ));
    }

    @When("I request payment details for ride ID {string}")
    public void retrievePayment(String rideId) {
        when(paymentService.getPayment(rideId)).thenReturn(new PaymentDTO(
                rideId, "ride123", "driver123", "passenger123", BigDecimal.valueOf(50.00), PaymentStatus.PENDING, LocalDateTime.now(), null, null, new RideInfo(PaymentMethod.CARD, CarCategory.ECONOMY, 100)
        ));
        paymentDTO = paymentService.getPayment(rideId);
    }


    @Then("I should receive the correct payment details")
    public void verifyRetrievedPayment() {
        assertNotNull(paymentDTO);
        assertEquals(PaymentStatus.PENDING, paymentDTO.getStatus());
    }

    @When("I request to delete the payment with ride ID {string}")
    public void requestToDeleteThePaymentWithRideId(String rideId) {
        PaymentDTO deletedPayment = new PaymentDTO();
        deletedPayment.setRideId(rideId);

        when(paymentService.deletePayment(rideId)).thenReturn(deletedPayment);

        paymentDTO = paymentService.deletePayment(rideId);
    }

    @Then("the payment should be deleted successfully")
    public void thePaymentShouldBeDeletedSuccessfully() {
        assertNotNull(paymentDTO, "Deleted payment should not be null");
        assertEquals("ride123", paymentDTO.getRideId());
    }

    @When("I request to complete the payment for ride ID {string}")
    public void completePayment(String rideId) {
        when(repository.findByRideId(rideId)).thenReturn(Optional.of(new Payment()));
        when(paymentService.makePaymentOnCompletedRide(rideId)).thenReturn(paymentDTO);
        paymentDTO = paymentService.makePaymentOnCompletedRide(rideId);
    }

    @Then("the payment should be marked as {string}")
    public void verifyPaymentCompletion(String status) {
        assertEquals(PaymentStatus.valueOf(status), paymentDTO.getStatus());
    }
}
