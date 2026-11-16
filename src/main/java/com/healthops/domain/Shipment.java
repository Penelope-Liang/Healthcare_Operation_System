package com.healthops.domain;

public record Shipment(String lot, String clinicName, String manufacturer, int doses) {
}
