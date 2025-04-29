package com.example.hellojava;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface KlipperApi {
    @GET("printer/info")
    Call<PrinterStatusResponse> getPrinterStatus();
    @GET("printer/objects/list")
    Call<ResponseBody> listObjects();


    @GET("server/files/download")
    Call<ResponseBody> downloadFile(@Query("path") String path);

    @GET("printer/objects/query")
    Call<PrinterObjectsResponse> queryObjects(@Query("objects") String objects);
}
