package com.example.hellojava;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface KlipperApi {
    @GET("printer/info")
    Call<PrinterStatusResponse> getPrinterStatus();

    @GET("server/files/download")
    Call<ResponseBody> downloadFile(@Query("path") String path);
}