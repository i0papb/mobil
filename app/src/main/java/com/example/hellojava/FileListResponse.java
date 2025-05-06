package com.example.hellojava;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/** Response for /server/files/list */
public class FileListResponse {
    @SerializedName("files")
    public List<FileEntry> files;

    public static class FileEntry {
        @SerializedName("filename")
        public String filename;
        // you can add size, modified, etc. here if desired
    }
}
