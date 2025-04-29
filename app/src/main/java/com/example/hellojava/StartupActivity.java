package com.example.hellojava;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartupActivity extends AppCompatActivity {
    private AutoCompleteTextView ipAddressInput;
    private AutoCompleteTextView portInput;
    private Button connectButton;
    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private ConnectionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        dbHelper = new DatabaseHelper(this);

        ipAddressInput = findViewById(R.id.ipAddressInput);
        portInput = findViewById(R.id.portInput);
        connectButton = findViewById(R.id.connectButton);
        recyclerView = findViewById(R.id.connectionsRecyclerView);

        List<String> pastIps = dbHelper.getAllHistoryIps();
        ipAddressInput.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, pastIps));

        List<Integer> pastPorts = dbHelper.getAllHistoryPorts();
        List<String> portStrings = new ArrayList<>();
        for (int p : pastPorts) portStrings.add(String.valueOf(p));
        portInput.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, portStrings));

        String lastIp = dbHelper.getLastIpAddress();
        String lastPort = dbHelper.getSetting("api_port");
        if (!TextUtils.isEmpty(lastIp)) ipAddressInput.setText(lastIp);
        if (!TextUtils.isEmpty(lastPort)) portInput.setText(lastPort);

        adapter = new ConnectionAdapter(this, pastIps, ip -> {
            dbHelper.deleteConnection(ip);
            recreate(); // refresh screen
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        connectButton.setOnClickListener(v -> {
            String ipStr = ipAddressInput.getText().toString().trim();
            String apiPortStr = portInput.getText().toString().trim();
            if (ipStr.isEmpty() || apiPortStr.isEmpty() || !TextUtils.isDigitsOnly(apiPortStr)) {
                Toast.makeText(this, R.string.error_enter_ip, Toast.LENGTH_SHORT).show();
                return;
            }
            dbHelper.setSetting("ip", ipStr);
            dbHelper.setSetting("api_port", apiPortStr);

            KlipperApi api = ApiClient.getApi(this);
            api.downloadFile("/etc/crowsnest/crowsnest.conf")
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                try {
                                    String text = response.body().string();
                                    int camPort = parsePortFromCrowsnest(text);
                                    dbHelper.setSetting("camera_port", String.valueOf(camPort));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            startMainActivity();
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(StartupActivity.this,
                                    "Failed to fetch remote config", Toast.LENGTH_SHORT).show();
                            startMainActivity();
                        }
                    });
        });
    }

    private int parsePortFromCrowsnest(String conf) {
        for (String line : conf.split("\\r?\\n")) {
            line = line.trim();
            if (line.startsWith("port:")) {
                try { return Integer.parseInt(line.substring(5).trim()); }
                catch (NumberFormatException ignored) {}
            }
        }
        return 8080;
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
