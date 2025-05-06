package com.example.hellojava;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

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
        // UI refs
        tvStatus      = findViewById(R.id.tv_status);
        progress      = findViewById(R.id.progress);
        btnRefresh    = findViewById(R.id.btn_refresh);
        spinnerPrints = findViewById(R.id.spinner_prints);
        btnStartPrint = findViewById(R.id.btn_start_print);

        // DB & API
        db = new DatabaseHelper(this);
        initApi();

        // Listeners
        btnRefresh.setOnClickListener(v -> fetchPrinterStatus());
        btnStartPrint.setOnClickListener(v -> {
            String file = (String) spinnerPrints.getSelectedItem();
            if (file == null || "No prints found".equals(file)) {
                Toast.makeText(this, "No print selected", Toast.LENGTH_SHORT).show();
            } else {
                startSelectedPrint(file);
            }
        });

        // Initial load
        fetchPrinterStatus();
        fetchUploadedPrints();
    }

    private void initApi() {
        String base = "http://" + db.getLastIpAddress() + ":" + db.getApiPort() + "/";
        api = new Retrofit.Builder()
                .baseUrl(base)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(KlipperApi.class);
    }

    // Inflate menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // Handle menu clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_home:
                fetchPrinterStatus();
                return true;
            case R.id.menu_status:
                startActivity(new Intent(this, StatusActivity.class));
                return true;
            case R.id.menu_objects:
                startActivity(new Intent(this, ObjectsActivity.class));
                return true;
            case R.id.action_change_ip:
                showChangeIpDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showChangeIpDialog() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Change Printer IP");
        EditText input = new EditText(this);
        input.setText(db.getLastIpAddress());
        b.setView(input);
        b.setPositiveButton("Save", (d, w) -> {
            String ip = input.getText().toString().trim();
            if (!ip.isEmpty()) {
                db.setSetting("ip", ip);
                initApi();
                fetchPrinterStatus();
            }
        });
        b.setNegativeButton("Cancel", null);
        b.show();
    }

    private void fetchPrinterStatus() {
        tvStatus.setVisibility(TextView.INVISIBLE);
        progress.setVisibility(ProgressBar.VISIBLE);
        api.getPrinterStatus().enqueue(new Callback<PrinterStatusResponse>() {
            @Override
            public void onResponse(Call<PrinterStatusResponse> c, Response<PrinterStatusResponse> r) {
                progress.setVisibility(ProgressBar.GONE);
                if (r.isSuccessful() && r.body() != null) {
                    tvStatus.setText("State: " + r.body().getState());
                } else {
                    tvStatus.setText("Error: " + r.code());
                }
                tvStatus.setVisibility(TextView.VISIBLE);
            }
            @Override
            public void onFailure(Call<PrinterStatusResponse> c, Throwable t) {
                progress.setVisibility(ProgressBar.GONE);
                tvStatus.setText("Network error");
                tvStatus.setVisibility(TextView.VISIBLE);
            }
        });
    }

    private void fetchUploadedPrints() {
        api.listFiles("gcodes").enqueue(new Callback<OctoFileListResponse>() {
            @Override
            public void onResponse(Call<OctoFileListResponse> c, Response<OctoFileListResponse> r) {
                if (!r.isSuccessful() || r.body() == null || r.body().result == null) {
                    Toast.makeText(MainActivity.this,
                            "List error: " + r.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                ArrayList<String> names = new ArrayList<>();
                for (OctoFileListResponse.FileEntry e : r.body().result) {
                    if (e.path != null) names.add(e.path);
                }
                if (names.isEmpty()) names.add("No prints found");
                ArrayAdapter<String> a = new ArrayAdapter<>(
                        MainActivity.this,
                        android.R.layout.simple_spinner_item,
                        names
                );
                a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerPrints.setAdapter(a);
            }
            @Override
            public void onFailure(Call<OctoFileListResponse> c, Throwable t) {
                Toast.makeText(MainActivity.this,
                        "Network error listing prints", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startSelectedPrint(String filename) {
        api.startPrint(filename).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> c, Response<Void> r) {
                Toast.makeText(MainActivity.this,
                        r.isSuccessful()
                                ? "Print started: " + filename
                                : "Start error: " + r.code(),
                        Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(Call<Void> c, Throwable t) {
                Toast.makeText(MainActivity.this,
                        "Network error starting print", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
