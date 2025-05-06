package com.example.hellojava;

import com.google.gson.annotations.SerializedName;

/** Wrapper for sending a single G-code/script line */
public class ScriptRequest {
    @SerializedName("script")
    private final String script;

    public ScriptRequest(String script) {
        this.script = script;
    }
}
