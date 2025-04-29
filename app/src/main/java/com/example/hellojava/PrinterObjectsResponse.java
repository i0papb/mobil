package com.example.hellojava;

import com.google.gson.annotations.SerializedName;

public class PrinterObjectsResponse {
    @SerializedName("result")
    private Result result;

    public Result getResult() {
        return result;
    }

    public static class Result {
        @SerializedName("status")
        private Status status;

        public Status getStatus() {
            return status;
        }
    }

    public static class Status {
        @SerializedName("extruder")
        private Extruder extruder;

        @SerializedName("heater_bed")
        private HeaterBed heaterBed;

        public Extruder getExtruder() {
            return extruder;
        }

        public HeaterBed getHeaterBed() {
            return heaterBed;
        }
    }

    public static class Extruder {
        @SerializedName("temperature")
        private float temperature;

        public float getTemperature() {
            return temperature;
        }
    }

    public static class HeaterBed {
        @SerializedName("temperature")
        private float temperature;

        public float getTemperature() {
            return temperature;
        }
    }
}
