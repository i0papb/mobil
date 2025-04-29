package com.example.hellojava;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private KlipperApi api;

    private TextView    tvStatus;
    private ProgressBar progress;
    private Button      btnRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db          = new DatabaseHelper(this);
        tvStatus    = findViewById(R.id.tv_status);
        progress    = findViewById(R.id.progress);
        btnRefresh  = findViewById(R.id.btn_refresh);

        initApi();
        btnRefresh.setOnClickListener(v -> fetchPrinterStatus());

        // initial load
        fetchPrinterStatus();
    }

    private void initApi() {
        String ip   = db.getLastIpAddress();
        int    port = db.getApiPort();
        String base = "http://" + ip + ":" + port + "/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(KlipperApi.class);
    }

    private void fetchPrinterStatus() {
        tvStatus.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.VISIBLE);

        Call<PrinterStatusResponse> call = api.getPrinterStatus();
        call.enqueue(new Callback<PrinterStatusResponse>() {
            @Override
            public void onResponse(Call<PrinterStatusResponse> call,
                                   Response<PrinterStatusResponse> res) {
                progress.setVisibility(View.GONE);
                if (!res.isSuccessful() || res.body() == null) {
                    Toast.makeText(MainActivity.this,
                            "API error: " + res.code(),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                String state = res.body().getState();
                tvStatus.setText("State: " + state);
                tvStatus.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<PrinterStatusResponse> call, Throwable t) {
                progress.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}