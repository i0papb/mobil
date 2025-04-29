package com.example.hellojava;

public class PrinterStatusResponse {
    public Status status;

    public static class Status {
        public HeaterBed heater_bed;
        public Extruder extruder;
    }

    public static class HeaterBed {
        public float temperature;
    }

    public static class Extruder {
        public float temperature;
    }
}

