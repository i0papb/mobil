package com.example.hellojava;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TextView extruderTemp;
    private TextView bedTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        extruderTemp = findViewById(R.id.extruderTemp);
        bedTemp = findViewById(R.id.bedTemp);

        // First discover what the printer supports
        discoverObjects();
    }

    private void discoverObjects() {
        KlipperApi api = ApiClient.getApi(this);

        api.listObjects().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String body = response.body().string();
                        Log.d(TAG, "Available objects: " + body);

                        // Once confirmed, query for temperature
                        fetchPrinterStatus();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Failed to discover objects", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Error listing objects", t);
                Toast.makeText(MainActivity.this, "Error discovering objects", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchPrinterStatus() {
        KlipperApi api = ApiClient.getApi(this);

        // Ask Moonraker for just the extruder and heater_bed blocks
        api.queryObjects("extruder,heater_bed").enqueue(new Callback<PrinterObjectsResponse>() {
            @Override
            public void onResponse(Call<PrinterObjectsResponse> call,
                                   Response<PrinterObjectsResponse> resp) {
                if (resp.isSuccessful() && resp.body() != null && resp.body().getResult() != null) {
                    PrinterObjectsResponse.Status st = resp.body().getResult().getStatus();

                    if (st.getExtruder() != null) {
                        extruderTemp.setText(getString(R.string.extruder_temp,
                                st.getExtruder().getTemperature()));
                    }

                    if (st.getHeaterBed() != null) {
                        bedTemp.setText(getString(R.string.bed_temp,
                                st.getHeaterBed().getTemperature()));
                    }

                    Log.d(TAG, "Printer status updated via objects/query");
                } else {
                    Log.w(TAG, "objects/query response not successful");
                    Toast.makeText(MainActivity.this,
                            "Failed to fetch status", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PrinterObjectsResponse> call, Throwable t) {
                Log.e(TAG, "Error querying objects", t);
                Toast.makeText(MainActivity.this,
                        "Error fetching printer status", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
