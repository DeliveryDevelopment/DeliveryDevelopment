package com.laioffer.delivery.geo;



import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.LatLng;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/geo")
@Validated
@RequiredArgsConstructor

public class GeoController {

    private final GeoService geoService;




    //===========================================

    /**
     * POST /geo/geocode
     *
     * Input:  JSON with "address"
     * Output: coordinates (lat, lng)
     */
    @PostMapping("/geocode")
    @ResponseStatus(HttpStatus.OK)
    public GeocodeResponse geocode(@Valid @RequestBody GeocodeRequest request) throws Exception {
        LatLng point = geoService.geocodeAddress(request.getAddress());
        return new GeocodeResponse(point.lat, point.lng);
    }

    /**
     * POST
     * driving-route
     *
     * driver route between two addresses.
     * Returns origin/destination coords, distance, duration, and path (list of LatLng points).
     */
    @PostMapping("/driving-route")
    @ResponseStatus(HttpStatus.OK)
    public DrivingRouteResponse drivingRoute(@Valid @RequestBody DrivingRouteRequest request) throws Exception {

        LatLng origin = geoService.geocodeAddress(request.getFromAddress());
        LatLng destination = geoService.geocodeAddress(request.getToAddress());

        GeoService.DrivingRouteResult routeResult = geoService.drivingRouteWithPath(origin, destination);

        List<LatLngPoint> path = routeResult.pathPoints().stream()
                .map(p -> new LatLngPoint(p.lat, p.lng))
                .toList();

        return new DrivingRouteResponse(
                origin.lat,
                origin.lng,
                destination.lat,
                destination.lng,
                routeResult.distanceMeters(),
                routeResult.durationSeconds(),
                path
        );
    }

    /**
     * POST /geo/straight-line
     *
     * Use-case: drone-style straight-line distance between two addresses.
     * Returns origin/destination coords and straight-line distance (meters).
     */
    @PostMapping("/straight-line")
    @ResponseStatus(HttpStatus.OK)
    public StraightLineResponse straightLine(@Valid @RequestBody DrivingRouteRequest request) throws Exception {
        LatLng origin = geoService.geocodeAddress(request.getFromAddress());
        LatLng destination = geoService.geocodeAddress(request.getToAddress());

        long distanceMeters = geoService.straightLineDistanceMeters(origin, destination);

        return new StraightLineResponse(
                origin.lat,
                origin.lng,
                destination.lat,
                destination.lng,
                distanceMeters
        );
    }

    // =====================

    @Data
    public static class GeocodeRequest {
        @NotBlank
        private String address;
    }

    public record GeocodeResponse(double lat, double lng) { }

    @Data
    public static class DrivingRouteRequest {
        @NotBlank
        private String fromAddress;

        @NotBlank
        private String toAddress;
    }

    public record LatLngPoint(double lat, double lng) { }

    public record DrivingRouteResponse(
            double fromLat,
            double fromLng,
            double toLat,
            double toLng,
            long distanceMeters,
            long durationSeconds,
            List<LatLngPoint> path
    ) { }


    public record StraightLineResponse(
            double fromLat,
            double fromLng,
            double toLat,
            double toLng,
            long distanceMeters
    ) { }


}
