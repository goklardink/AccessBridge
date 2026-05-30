package com.example.accessbridgeproject.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.accessbridgeproject.R;
import com.example.accessbridgeproject.adapters.InfoAdapter;
import com.example.accessbridgeproject.models.DurakFeature;
import com.example.accessbridgeproject.models.DurakResponse;
import com.example.accessbridgeproject.models.InfoItem;
import com.example.accessbridgeproject.network.RetrofitClient;
import com.example.accessbridgeproject.utils.NetworkReceiver;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransportActivity extends BaseActivity {

    private NetworkReceiver networkReceiver;
    private IntentFilter intentFilter;
    RecyclerView recyclerView;
    InfoAdapter adapter;
    List<InfoItem> itemList;
    List<DurakFeature> allStops;
    TextView btnBack;
    AppCompatEditText etSearch;
    Button btnSearch, btnStops, btnLines;
    ProgressBar progressBar;
    private String currentMode = "stops";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport);

        btnBack = findViewById(R.id.btnBack);
        recyclerView = findViewById(R.id.recyclerViewTransport);
        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        btnStops = findViewById(R.id.btnStops);
        btnLines = findViewById(R.id.btnLines);
        progressBar = findViewById(R.id.progressBar);

        btnBack.setOnClickListener(v -> finish());

        itemList = new ArrayList<>();
        allStops = new ArrayList<>();

        adapter = new InfoAdapter(this, itemList, item -> {
            String mapQuery = Uri.encode(item.getTitle() + " istanbul");
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/maps/search/" + mapQuery));
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnStops.setOnClickListener(v -> {
            currentMode = "stops";
            updateTabColors(true);
            etSearch.setHint("Search by stop name...");
            filterStops(etSearch.getText().toString().trim());
        });

        btnLines.setOnClickListener(v -> {
            currentMode = "directions";
            updateTabColors(false);
            etSearch.setHint("Search by direction (e.g. KARTAL)...");
            filterByDirection(etSearch.getText().toString().trim());
        });

        btnSearch.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim();
            if (query.isEmpty()) {
                Toast.makeText(this, "Please enter a search term", Toast.LENGTH_SHORT).show();
                return;
            }
            if (currentMode.equals("stops")) {
                filterStops(query);
            } else {
                filterByDirection(query);
            }
        });

        networkReceiver = new NetworkReceiver();
        intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

        updateTabColors(true);
        loadStops();
    }

    private void loadStops() {
        progressBar.setVisibility(View.VISIBLE);
        itemList.clear();
        adapter.notifyDataSetChanged();

        RetrofitClient.getService().getStops().enqueue(new Callback<DurakResponse>() {
            @Override
            public void onResponse(Call<DurakResponse> call, Response<DurakResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    allStops = response.body().getFeatures();
                    showStops(allStops);
                } else {
                    Toast.makeText(TransportActivity.this,
                            "Server error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DurakResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(TransportActivity.this,
                        "Connection error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterStops(String query) {
        if (allStops.isEmpty()) {
            Toast.makeText(this, "Data not loaded yet", Toast.LENGTH_SHORT).show();
            return;
        }
        itemList.clear();
        for (DurakFeature feature : allStops) {
            String name = feature.getProperties().getName();
            if (name != null && name.toLowerCase().contains(query.toLowerCase())) {
                String description = "🚌 " + feature.getProperties().getStopType()
                        + "\n📍 Direction: " + feature.getProperties().getDirection();
                itemList.add(new InfoItem(name, description, "transport"));
            }
        }
        adapter.notifyDataSetChanged();
        if (itemList.isEmpty()) {
            Toast.makeText(this, "No stops found", Toast.LENGTH_SHORT).show();
        }
    }

    private void filterByDirection(String query) {
        if (allStops.isEmpty()) {
            Toast.makeText(this, "Data not loaded yet", Toast.LENGTH_SHORT).show();
            return;
        }
        itemList.clear();
        for (DurakFeature feature : allStops) {
            String direction = feature.getProperties().getDirection();
            if (direction != null && direction.toLowerCase().contains(query.toLowerCase())) {
                String name = feature.getProperties().getName();
                String description = "🚌 " + feature.getProperties().getStopType()
                        + "\n📍 Direction: " + direction;
                itemList.add(new InfoItem(name, description, "transport"));
            }
        }
        adapter.notifyDataSetChanged();
        if (itemList.isEmpty()) {
            Toast.makeText(this, "No stops found for this direction", Toast.LENGTH_SHORT).show();
        }
    }

    private void showStops(List<DurakFeature> stops) {
        itemList.clear();
        for (DurakFeature feature : stops) {
            String name = feature.getProperties().getName();
            String direction = feature.getProperties().getDirection();
            String type = feature.getProperties().getStopType();
            String description = "🚌 " + type + "\n📍 Direction: " + direction;
            itemList.add(new InfoItem(name, description, "transport"));
        }
        adapter.notifyDataSetChanged();
    }

    private void updateTabColors(boolean stopsSelected) {
        if (stopsSelected) {
            btnStops.setBackgroundColor(getResources().getColor(R.color.color_transport));
            btnStops.setTextColor(getResources().getColor(R.color.white));
            btnLines.setBackgroundColor(getResources().getColor(R.color.background_light));
            btnLines.setTextColor(getResources().getColor(R.color.text_primary));
        } else {
            btnLines.setBackgroundColor(getResources().getColor(R.color.color_transport));
            btnLines.setTextColor(getResources().getColor(R.color.white));
            btnStops.setBackgroundColor(getResources().getColor(R.color.background_light));
            btnStops.setTextColor(getResources().getColor(R.color.text_primary));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(networkReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkReceiver);
    }
}