package com.example.hellojava;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ObjectsActivity extends AppCompatActivity {
    private TextView    tvPrintState, tvPrintFile, tvSdProgress;
    private ProgressBar pbSdProgress;
    private KlipperApi  api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_objects);

        tvPrintState = findViewById(R.id.tv_print_state);
        tvPrintFile  = findViewById(R.id.tv_print_file);
        tvSdProgress = findViewById(R.id.tv_sd_progress);
        pbSdProgress = findViewById(R.id.pb_sd_progress);

        // Build the Retrofit API client
        DatabaseHelper db = new DatabaseHelper(this);
        String base = "http://" + db.getLastIpAddress() + ":" + db.getApiPort() + "/";
        api = new Retrofit.Builder()
                .baseUrl(base)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(KlipperApi.class);

        fetchPrintObjects();
    }

    private void fetchPrintObjects() {
        api.queryObjects("print_stats,virtual_sdcard")
                .enqueue(new Callback<ObjectsQueryResponse>() {
                    @Override
                    public void onResponse(Call<ObjectsQueryResponse> call,
                                           Response<ObjectsQueryResponse> res) {
                        if (!res.isSuccessful() || res.body() == null) {
                            Toast.makeText(ObjectsActivity.this,
                                    "Error: " + res.code(),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // Populate the views
                        ObjectsQueryResponse.Result r = res.body().result;
                        tvPrintState .setText("State: " + r.printStats.state);
                        tvPrintFile  .setText("File:  " + r.printStats.filename);
                        int pct = (int)(r.virtualSdcard.progress * 100f);
                        tvSdProgress .setText("SD:    " + pct + "%");
                        pbSdProgress .setProgress(pct);
                    }

                    @Override
                    public void onFailure(Call<ObjectsQueryResponse> call, Throwable t) {
                        Toast.makeText(ObjectsActivity.this,
                                "Network error",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
