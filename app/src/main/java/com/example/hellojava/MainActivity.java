package com.example.hellojava;

import android.os.Bundle;
import android.view.View;
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

        // --- find views ---
        tvStatus       = findViewById(R.id.tv_status);
        progress       = findViewById(R.id.progress);
        btnRefresh     = findViewById(R.id.btn_refresh);
        spinnerPrints  = findViewById(R.id.spinner_prints);
        btnStartPrint  = findViewById(R.id.btn_start_print);

        // --- init DB & API ---
        db  = new DatabaseHelper(this);
        initApi();

        // --- set listeners ---
        btnRefresh.setOnClickListener(v -> fetchPrinterStatus());
        btnStartPrint.setOnClickListener(v -> {
            String file = (String) spinnerPrints.getSelectedItem();
            if (file == null) {
                Toast.makeText(this, "No print selected", Toast.LENGTH_SHORT).show();
            } else {
                startSelectedPrint(file);
            }
        });

        // --- initial data load ---
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

    /** Fetch Klipper state (ready/error) */
    private void fetchUploadedPrints() {
        api.listFiles("gcodes").enqueue(new Callback<FileListResponse>() {
            @Override
            public void onResponse(Call<FileListResponse> call,
                                   Response<FileListResponse> res) {
                // 1) Bail if there’s no body or no files list
                FileListResponse body = res.body();
                if (!res.isSuccessful() || body == null || body.files == null) {
                    Toast.makeText(MainActivity.this,
                            "Failed to list prints: " + res.code(),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // 2) Safely build our name list
                List<String> names = new ArrayList<>(body.files.size());
                for (FileListResponse.FileEntry e : body.files) {
                    if (e != null && e.filename != null) {
                        names.add(e.filename);
                    }
                }

                // 3) If still empty, show a placeholder
                if (names.isEmpty()) {
                    names.add("No prints found");
                }

                // 4) Populate the spinner
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
            public void onFailure(Call<FileListResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this,
                        "Network error listing prints: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** Populate the spinner with all .gcode files on the printer */
    private void fetchUploadedPrints() {
        api.listFiles("gcodes").enqueue(new Callback<FileListResponse>() {
            @Override
            public void onResponse(Call<FileListResponse> call,
                                   Response<FileListResponse> res) {
                if (res.isSuccessful() && res.body() != null) {
                    List<String> names = new ArrayList<>();
                    for (FileListResponse.FileEntry e : res.body().files) {
                        names.add(e.filename);
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
                } else {
                    Toast.makeText(MainActivity.this,
                            "Failed to list prints: " + res.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FileListResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this,
                        "Network error listing prints: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** Ask Moonraker to start the selected print */
    private void startSelectedPrint(String filename) {
        api.startPrint(filename).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> res) {
                if (res.isSuccessful()) {
                    Toast.makeText(MainActivity.this,
                            "Print started: " + filename,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this,
                            "Failed to start print: " + res.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
