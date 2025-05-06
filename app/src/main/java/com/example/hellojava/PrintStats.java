package com.example.hellojava;

import com.google.gson.annotations.SerializedName;

/** Part of ObjectsQueryResponse.result.print_stats */
public class PrintStats {
    @SerializedName("state")
    public String state;

    @SerializedName("filename")
    public String filename;

    @SerializedName("print_duration")
    public float printDuration;

    @SerializedName("filament_loaded")
    public float filamentLoaded;
}
