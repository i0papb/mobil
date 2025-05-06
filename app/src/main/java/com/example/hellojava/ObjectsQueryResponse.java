package com.example.hellojava;

import com.google.gson.annotations.SerializedName;

/** Response for /printer/objects/query?objects=… */
public class ObjectsQueryResponse {
    @SerializedName("result")
    public Result result;

    public static class Result {
        @SerializedName("print_stats")
        public PrintStats printStats;

        @SerializedName("virtual_sdcard")
        public VirtualSdCard virtualSdcard;
    }
}
