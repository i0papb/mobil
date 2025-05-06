package com.example.hellojava;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface KlipperApi {
    /** Basic printer state (ready/error) */
    @GET("/printer/info")
    Call<PrinterStatusResponse> getPrinterStatus();

    /** List files under a given SD root (OctoPrint style) */
    @GET("/server/files/list")
    Call<OctoFileListResponse> listFiles(@Query("root") String root);

    /** Start printing the specified file */
    @POST("/printer/print/start")
    Call<Void> startPrint(@Query("filename") String filename);

    /** Send one G-code/script line to the Klipper console */
    @POST("/printer/gcode/script")
    Call<Void> runGcodeScript(@Body ScriptRequest body);

    /** Query arbitrary printer objects (e.g. print_stats, virtual_sdcard) */
    @GET("/printer/objects/query")
    Call<ObjectsQueryResponse> queryObjects(@Query("objects") String objects);

    /** NEW: Query temperatures and fans */
    @GET("/printer/objects/query")
    Call<TempFanResponse> queryTempFan(@Query("objects") String objects);
}
