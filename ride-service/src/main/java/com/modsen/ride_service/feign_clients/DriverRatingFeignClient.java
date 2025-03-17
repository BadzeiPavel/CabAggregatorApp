package com.modsen.ride_service.feign_clients;

import models.dtos.RatingStatisticResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "driver-rating-controller", url = "http://localhost:8084/api/v1/ratings/drivers")
public interface DriverRatingFeignClient {

    @GetMapping("/{driverId}/statistic")
    public ResponseEntity<RatingStatisticResponseDTO> getDriverRatingStatistic(@PathVariable String driverId);
}
