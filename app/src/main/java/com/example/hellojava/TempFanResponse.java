package com.example.hellojava;

import com.google.gson.annotations.SerializedName;

/** Response for querying bed/extruder temps & fan speed */
public class TempFanResponse {
    @SerializedName("result")
    public Result result;

    public static class Result {
        @SerializedName("heater_bed")
        public Heater bed;

        @SerializedName("extruder")
        public Heater extruder;

        @SerializedName("fan")
        public Fan fan;
    }

    public static class Heater {
        @SerializedName("temperature")
        public float temperature;
        @SerializedName("target")
        public float target;
    }

    public static class Fan {
        @SerializedName("speed")
        public float speed;  // 0.0–1.0
    }
}
