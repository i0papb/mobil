package com.example.hellojava;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StatusActivity extends AppCompatActivity {
    private TextView tvBed, tvExtruder, tvFan;
    private Button   btnRefresh;
    private KlipperApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        tvBed      = findViewById(R.id.tv_bed);
        tvExtruder = findViewById(R.id.tv_extruder);
        tvFan      = findViewById(R.id.tv_fan);
        btnRefresh = findViewById(R.id.btn_refresh_status);

        DatabaseHelper db = new DatabaseHelper(this);
        String base = "http://" + db.getLastIpAddress() + ":" + db.getApiPort() + "/";
        api = new Retrofit.Builder()
                .baseUrl(base)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(KlipperApi.class);

        btnRefresh.setOnClickListener(v -> fetchTempsAndFan());
        fetchTempsAndFan();
    }

    private void fetchTempsAndFan() {
        api.queryTempFan("heater_bed,extruder,fan")
                .enqueue(new Callback<TempFanResponse>() {
                    @Override
                    public void onResponse(Call<TempFanResponse> call,
                                           Response<TempFanResponse> res) {
                        if (!res.isSuccessful() || res.body() == null) {
                            Toast.makeText(StatusActivity.this,
                                    "Error: " + res.code(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        TempFanResponse.Result r = res.body().result;
                        tvBed.setText(String.format(
                                "Bed: %.1f / %.0f °C",
                                r.bed.temperature, r.bed.target));
                        tvExtruder.setText(String.format(
                                "Extruder: %.1f / %.0f °C",
                                r.extruder.temperature, r.extruder.target));
                        tvFan.setText(String.format(
                                "Fan: %.0f%%", r.fan.speed * 100f));
                    }
                    @Override public void onFailure(Call<TempFanResponse> call, Throwable t) {
                        Toast.makeText(StatusActivity.this,
                                "Network error", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
