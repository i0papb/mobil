package com.example.hellojava;

import com.google.gson.annotations.SerializedName;

public class PrinterStatusResponse {

    @SerializedName("result")
    private Result result;

    /** Safely get the printer state (e.g. "ready") */
    public String getState() {
        return result != null ? result.state : null;
    }

    /** Get the human-readable message */
    public String getStateMessage() {
        return result != null ? result.stateMessage : null;
    }

    /** Get other fields as needed… */
    public String getHostname() {
        return result != null ? result.hostname : null;
    }

    // add getters for any other properties you care about

    public static class Result {
        @SerializedName("state")
        String state;

        @SerializedName("state_message")
        String stateMessage;

        @SerializedName("hostname")
        String hostname;

        @SerializedName("klipper_path")
        String klipperPath;

        @SerializedName("python_path")
        String pythonPath;

        @SerializedName("process_id")
        int processId;

        @SerializedName("user_id")
        int userId;

        @SerializedName("group_id")
        int groupId;

        @SerializedName("log_file")
        String logFile;

        @SerializedName("config_file")
        String configFile;

        @SerializedName("software_version")
        String softwareVersion;

        @SerializedName("cpu_info")
        String cpuInfo;
    }
}
