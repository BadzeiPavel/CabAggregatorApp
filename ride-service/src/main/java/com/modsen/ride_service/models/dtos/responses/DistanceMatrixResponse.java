package com.modsen.ride_service.models.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DistanceMatrixResponse {
    @JsonProperty("resourceSets")
    private List<ResourceSet> resourceSets;

    @Data
    public static class ResourceSet {
        @JsonProperty("resources")
        private List<Resource> resources;
    }

    @Data
    public static class Resource {
        @JsonProperty("results")
        private List<Result> results;
    }

    @Data
    public static class Result {
        @JsonProperty("travelDistance")
        private double travelDistance;
    }
}
