package com.example.hellojava;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StartupActivity extends AppCompatActivity {
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        db = new DatabaseHelper(this);
        List<Connection> history = db.getRecentConnections();

        RecyclerView rv = findViewById(R.id.recycler_history);
        rv.setLayoutManager(new LinearLayoutManager(this));
        ConnectionAdapter adapter = new ConnectionAdapter(
                history,
                conn -> {
                    // on click: save as last-used and go to MainActivity
                    db.setSetting("ip", conn.getIp());
                    db.setSetting("api_port", String.valueOf(conn.getPort()));
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                });
        rv.setAdapter(adapter);

        // optionally, a button to open ConnectActivity for a new entry
        findViewById(R.id.btn_new).setOnClickListener(v ->
                startActivity(new Intent(this, ConnectActivity.class))
        );
    }
}