package com.example.hellojava;

import com.google.gson.annotations.SerializedName;

/** Part of ObjectsQueryResponse.result.virtual_sdcard */
public class VirtualSdCard {
    @SerializedName("is_active")
    public boolean isActive;

    @SerializedName("progress")
    public float progress;
}
