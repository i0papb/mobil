package com.example.hellojava;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private KlipperApi     api;

    private TextView    tvStatus;
    private ProgressBar progress;
    private Button      btnRefresh, btnStartPrint;
    private Spinner     spinnerPrints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find UI elements
        tvStatus      = findViewById(R.id.tv_status);
        progress      = findViewById(R.id.progress);
        btnRefresh    = findViewById(R.id.btn_refresh);
        spinnerPrints = findViewById(R.id.spinner_prints);
        btnStartPrint = findViewById(R.id.btn_start_print);

        // Init database & API
        db = new DatabaseHelper(this);
        initApi();

        // Wire up buttons
        btnRefresh.setOnClickListener(v -> fetchPrinterStatus());
        btnStartPrint.setOnClickListener(v -> {
            String file = (String) spinnerPrints.getSelectedItem();
            if (file == null || file.equals("No prints found")) {
                Toast.makeText(this, "No print selected", Toast.LENGTH_SHORT).show();
            } else {
                startSelectedPrint(file);
            }
        });

        // Initial loads
        fetchPrinterStatus();
        fetchUploadedPrints();
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

    /** GET /printer/info */
    private void fetchPrinterStatus() {
        tvStatus.setVisibility(TextView.INVISIBLE);
        progress.setVisibility(ProgressBar.VISIBLE);

        api.getPrinterStatus().enqueue(new Callback<PrinterStatusResponse>() {
            @Override
            public void onResponse(Call<PrinterStatusResponse> call,
                                   Response<PrinterStatusResponse> res) {
                progress.setVisibility(ProgressBar.GONE);
                if (res.isSuccessful() && res.body() != null) {
                    String state = res.body().getState();
                    tvStatus.setText("State: " + state);
                } else {
                    tvStatus.setText("Error: " + res.code());
                }
                tvStatus.setVisibility(TextView.VISIBLE);
            }

            @Override
            public void onFailure(Call<PrinterStatusResponse> call, Throwable t) {
                progress.setVisibility(ProgressBar.GONE);
                tvStatus.setText("Network error");
                tvStatus.setVisibility(TextView.VISIBLE);
            }
        });
    }

    /** GET /server/files/list?root=gcodes */
    private void fetchUploadedPrints() {
        api.listFiles("gcodes").enqueue(new Callback<OctoFileListResponse>() {
            @Override
            public void onResponse(Call<OctoFileListResponse> call,
                                   Response<OctoFileListResponse> res) {
                if (!res.isSuccessful() || res.body() == null || res.body().result == null) {
                    Toast.makeText(MainActivity.this,
                            "List error: " + res.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                List<String> names = new ArrayList<>();
                for (OctoFileListResponse.FileEntry e : res.body().result) {
                    if (e.path != null) names.add(e.path);
                }
                if (names.isEmpty()) {
                    names.add("No prints found");
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        MainActivity.this,
                        android.R.layout.simple_spinner_item,
                        names
                );
                adapter.setDropDownViewResource(
                        android.R.layout.simple_spinner_dropdown_item
                );
                spinnerPrints.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<OctoFileListResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this,
                        "Network error listing prints: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** POST /printer/print/start?filename=… */
    private void startSelectedPrint(String filename) {
        api.startPrint(filename).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> res) {
                if (res.isSuccessful()) {
                    Toast.makeText(MainActivity.this,
                            "Print started: " + filename, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this,
                            "Start error: " + res.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this,
                        "Network error starting print", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
