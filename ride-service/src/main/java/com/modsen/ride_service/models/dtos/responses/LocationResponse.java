package com.modsen.ride_service.models.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class LocationResponse {
    @JsonProperty("resourceSets")
    private List<ResourceSet> resourceSets;

    @Data
    public static class ResourceSet {
        @JsonProperty("resources")
        private List<Resource> resources;
    }

    @Data
    public static class Resource {
        @JsonProperty("address")
        private Address address;
    }

    @Data
    public static class Address {
        @JsonProperty("formattedAddress")
        private String formattedAddress;
    }
}
