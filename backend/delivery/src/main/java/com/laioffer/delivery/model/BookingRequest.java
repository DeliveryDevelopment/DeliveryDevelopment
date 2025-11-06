package com.laioffer.delivery.model;


import java.time.LocalDate;


public record BookingRequest(
        long listingId,
        LocalDate checkInDate,
        LocalDate checkOutDate
) {
}
