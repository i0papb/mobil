package com.example.hellojava;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class ConnectActivity extends AppCompatActivity {
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        db = new DatabaseHelper(this);
        EditText ipInput   = findViewById(R.id.ip_input);
        EditText portInput = findViewById(R.id.port_input);
        Button   goButton  = findViewById(R.id.go_button);

        goButton.setOnClickListener(v -> {
            String ip = ipInput.getText().toString().trim();
            int port;
            try {
                port = Integer.parseInt(portInput.getText().toString().trim());
            } catch (NumberFormatException e) {
                port = db.getApiPort(); // fallback
            }

            // persist
            db.setSetting("ip", ip);
            db.setSetting("api_port", String.valueOf(port));
            db.addConnectionToHistory(ip, port);

            // next screen
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}