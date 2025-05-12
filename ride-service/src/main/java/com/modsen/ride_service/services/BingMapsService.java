package com.modsen.ride_service.services;

import com.modsen.ride_service.models.dtos.RideDTO;
import com.modsen.ride_service.models.dtos.responses.DistanceMatrixResponse;
import com.modsen.ride_service.models.dtos.responses.LocationResponse;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BingMapsService {

    private final RestTemplate restTemplate;

    @Setter
    @Value("${bing-maps.api-key}")
    private String API_KEY;
    private static final String DISTANCE_MATRIX_URL = "http://dev.virtualearth.net/REST/v1/Routes/DistanceMatrix";
    private static final String LOCATION_URL = "http://dev.virtualearth.net/REST/v1/Locations";

    public Map<String, String> getRideDetails(RideDTO ride) {
        Map<String, String> result = new HashMap<>();

        DistanceMatrixResponse distanceResponse = getDistanceMatrix(
                ride.getOriginLatitude(), ride.getOriginLongitude(),
                ride.getDestinationLatitude(), ride.getDestinationLongitude()
        );
        double distance = distanceResponse.getResourceSets().get(0).getResources().get(0).getResults().get(0).getTravelDistance();
        result.put("distance", String.valueOf(distance));

        LocationResponse originLocation = getAddress(ride.getOriginLatitude(), ride.getOriginLongitude());
        String originAddress = originLocation.getResourceSets().get(0).getResources().get(0).getAddress().getFormattedAddress();
        result.put("originAddress", originAddress);

        LocationResponse destinationLocation = getAddress(ride.getDestinationLatitude(), ride.getDestinationLongitude());
        String destinationAddress = destinationLocation.getResourceSets().get(0).getResources().get(0).getAddress().getFormattedAddress();
        result.put("destinationAddress", destinationAddress);

        return result;
    }


    private DistanceMatrixResponse getDistanceMatrix(double originLat, double originLng, double destLat, double destLng) {
        Map<String, String> params = new HashMap<>();
        params.put("origins", originLat + "," + originLng);
        params.put("destinations", destLat + "," + destLng);
        params.put("travelMode", "driving");
        params.put("key", API_KEY);

        String url = UriComponentsBuilder.fromUriString(DISTANCE_MATRIX_URL)
                .queryParam("origins", "{origins}")
                .queryParam("destinations", "{destinations}")
                .queryParam("travelMode", "{travelMode}")
                .queryParam("key", "{key}")
                .encode()
                .toUriString();

        return restTemplate.getForObject(url, DistanceMatrixResponse.class, params);
    }

    private LocationResponse getAddress(double lat, double lng) {
        Map<String, String> params = new HashMap<>();
        params.put("latitude", String.valueOf(lat));
        params.put("longitude", String.valueOf(lng));
        params.put("key", API_KEY);

        String url = UriComponentsBuilder.fromUriString(LOCATION_URL)
                .path("/{latitude},{longitude}")
                .queryParam("key", "{key}")
                .encode()
                .toUriString();

        return restTemplate.getForObject(url, LocationResponse.class, params);
    }
}
