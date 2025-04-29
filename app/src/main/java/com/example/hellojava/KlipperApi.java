package com.example.hellojava;

import retrofit2.Call;
import retrofit2.http.GET;

public interface KlipperApi {
    @GET("/printer/info")
    Call<PrinterStatusResponse> getPrinterStatus();
}
