package com.example.hellojava;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class FileListResponse {
    @SerializedName("files")
    public List<FileEntry> files;

    public static class FileEntry {
        @SerializedName("filename")
        public String filename;
        // You can add size/date if you like:
        // @SerializedName("modified")
        // public String modified;
    }
}
