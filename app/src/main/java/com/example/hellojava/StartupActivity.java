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
        portInput      = findViewById(R.id.portInput);
        connectButton  = findView
