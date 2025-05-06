package com.example.hellojava;

import com.google.gson.annotations.SerializedName;

/** Response for /printer/info */
public class PrinterStatusResponse {
    @SerializedName("result")
    private Result result;

    public String getState() {
        return result != null ? result.state : null;
    }

    public static class Result {
        @SerializedName("state")
        String state;
    }
}
