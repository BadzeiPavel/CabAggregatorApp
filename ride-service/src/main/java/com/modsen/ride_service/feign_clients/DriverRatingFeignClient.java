package com.modsen.ride_service.feign_clients;

import models.dtos.RatingStatisticResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "rating-service")
public interface DriverRatingFeignClient {

    @GetMapping("/api/v1/ratings/drivers/{driverId}/statistic")
    public ResponseEntity<RatingStatisticResponseDTO> getDriverRatingStatistic(@PathVariable String driverId);
}
