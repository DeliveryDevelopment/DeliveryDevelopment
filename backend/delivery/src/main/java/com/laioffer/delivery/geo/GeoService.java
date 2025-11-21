package com.laioffer.delivery.geo;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;


@Service
@RequiredArgsConstructor
public class GeoService {

    private final GeoApiContext geoApiContext;

    //==============================

    /**
     * address into coordinates using Geocoding API
     * return LatLng with latitude and longitude
     */
    public LatLng geocodeAddress(String address) throws Exception {
        if (!StringUtils.hasText(address)) throw new IllegalArgumentException("address empty");

        GeocodingResult[] results = GeocodingApi
                .geocode(geoApiContext, address)
                .await();

        if (results == null || results.length == 0) {
            throw new IllegalArgumentException("not such Address: " + address);
        }

        return results[0].geometry.location; // return the first as position
    }

    // dto
    /**
     * A compact summary of a driving route, including path points.
     */
    public record DrivingRouteResult(
            long distanceMeters,
            long durationSeconds,
            List<LatLng> pathPoints
    ) { }

    /**
     * Convenience method:
     * with given from address and the to address , return the dto DrivingRouteResult directly
     */
    public DrivingRouteResult drivingRouteForAddresses(String fromAddress, String toAddress) throws Exception {
        LatLng origin = geocodeAddress(fromAddress);
        LatLng destination = geocodeAddress(toAddress);
        return drivingRouteWithPath(origin, destination);
    }


    public DrivingRouteResult drivingRouteWithPath(LatLng origin, LatLng destination) throws Exception {
        DirectionsResult result = DirectionsApi
                .newRequest(geoApiContext)
                .origin(origin)
                .destination(destination)
                .mode(TravelMode.DRIVING)
                .await();

        if (result.routes == null || result.routes.length == 0) {
            throw new IllegalStateException("No route found between the given points");
        }

        DirectionsRoute route = result.routes[0];

        if (route.legs == null || route.legs.length == 0) {
            throw new IllegalStateException("Route has no legs ( section of path )");
        }

        // Take the first result as we only have one "leg"
        // a "leg" in google map is a section of path ( allow stop at gas station etc . here we do not use more than one)
        DirectionsLeg leg = route.legs[0];

        long distanceMeters = leg.distance.inMeters;
        long durationSeconds = leg.duration.inSeconds;

        List<LatLng> pathPoints = route.overviewPolyline.decodePath();

        return new DrivingRouteResult(distanceMeters, durationSeconds, pathPoints);
    }

    /**
     * Straight distance in meters between two coordinates.
     *  computed locally the drone paths distance ==> return in Long ( meter )
     */
    public long straightLineDistanceMeters(LatLng a, LatLng b) {
        double earthRadiusMeters = 6371_000.0;

        double lat1 = Math.toRadians(a.lat);
        double lat2 = Math.toRadians(b.lat);
        double dLat = lat2 - lat1;
        double dLng = Math.toRadians(b.lng - a.lng);

        double sinLat = Math.sin(dLat / 2.0);
        double sinLng = Math.sin(dLng / 2.0);

        double aa = sinLat * sinLat
                + Math.cos(lat1) * Math.cos(lat2) * sinLng * sinLng;

        double c = 2.0 * Math.atan2(Math.sqrt(aa), Math.sqrt(1.0 - aa));

        double distanceMeters = earthRadiusMeters * c;

        return Math.round(distanceMeters);
    }


}
