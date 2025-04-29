package com.example.hellojava;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StartupActivity extends AppCompatActivity {

    private RecyclerView recentConnectionsRecyclerView;
    private RecentConnectionsAdapter adapter;
    private List<Connection> recentConnectionsList;
    private DatabaseHelper databaseHelper;
    private Button connectBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        connectBtn = findViewById(R.id.connectBtn);
        recentConnectionsRecyclerView = findViewById(R.id.recentConnectionsRecyclerView);

        // Set up the layout manager
        recentConnectionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize database helper and get recent connections
        databaseHelper = new DatabaseHelper(this);
        recentConnectionsList = databaseHelper.getRecentConnections();

        // Log list size for debugging
        Log.d("StartupActivity", "Recent connections count: " + recentConnectionsList.size());

        // Set up adapter with the list
        adapter = new RecentConnectionsAdapter(recentConnectionsList);
        recentConnectionsRecyclerView.setAdapter(adapter);

        // Notify adapter to refresh UI
        adapter.notifyDataSetChanged();

        // Handle button click
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartupActivity.this, ConnectActivity.class);
                startActivity(intent);
            }
        });
    }
}
