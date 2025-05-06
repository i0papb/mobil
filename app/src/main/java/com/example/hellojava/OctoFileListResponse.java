package com.example.hellojava;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Matches OctoPrint-style JSON:
 *   { "result": [ { "path": "...", ... }, ... ] }
 */
public class OctoFileListResponse {
    @SerializedName("result")
    public List<FileEntry> result;

    public static class FileEntry {
        @SerializedName("path")
        public String path;

        @SerializedName("modified")
        public double modified;      // timestamp

        @SerializedName("size")
        public long size;           // bytes

        @SerializedName("permissions")
        public String permissions;  // e.g. "rw"
    }
}
