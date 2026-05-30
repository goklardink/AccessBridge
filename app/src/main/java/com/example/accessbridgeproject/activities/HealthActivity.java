package com.example.accessbridgeproject.activities;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
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
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.accessbridgeproject.R;
import com.example.accessbridgeproject.adapters.InfoAdapter;
import com.example.accessbridgeproject.models.InfoItem;
import com.example.accessbridgeproject.utils.NetworkReceiver;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HealthActivity extends BaseActivity {

    private NetworkReceiver networkReceiver;
    private IntentFilter intentFilter;
    RecyclerView recyclerView;
    InfoAdapter adapter;
    List<InfoItem> itemList;
    TextView btnBack;
    AppCompatEditText etSearch;
    Button btnSearch, btnHospital, btnPharmacy;
    ProgressBar progressBar;

    private double currentLat = 41.0082;
    private double currentLon = 28.9784;
    private String currentType = "hospital";
    private static final int LOCATION_PERMISSION_REQUEST = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);

        btnBack = findViewById(R.id.btnBack);
        recyclerView = findViewById(R.id.recyclerViewHealth);
        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        btnHospital = findViewById(R.id.btnHospital);
        btnPharmacy = findViewById(R.id.btnPharmacy);
        progressBar = findViewById(R.id.progressBar);

        btnBack.setOnClickListener(v -> finish());

        itemList = new ArrayList<>();

        adapter = new InfoAdapter(this, itemList, item -> {
            // Karta basınca Google Maps'te aç
            String mapQuery = Uri.encode(item.getTitle() + " " + item.getCategory());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("geo:" + currentLat + "," + currentLon +
                            "?q=" + mapQuery));
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com/maps/search/" +
                                mapQuery));
                startActivity(browserIntent);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnHospital.setOnClickListener(v -> {
            currentType = "hospital";
            updateTabColors(true);
            searchHealthFacilities(currentLat, currentLon, currentType);
        });

        btnPharmacy.setOnClickListener(v -> {
            currentType = "pharmacy";
            updateTabColors(false);
            searchHealthFacilities(currentLat, currentLon, currentType);
        });

        btnSearch.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim();
            if (!query.isEmpty()) {
                searchBySemt(query);
            } else {
                searchHealthFacilities(currentLat, currentLon, currentType);
            }
        });

        networkReceiver = new NetworkReceiver();
        intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

        requestLocationAndSearch();
    }

    private void updateTabColors(boolean hospitalSelected) {
        if (hospitalSelected) {
            btnHospital.setBackgroundColor(getResources().getColor(R.color.color_health));
            btnHospital.setTextColor(getResources().getColor(R.color.white));
            btnPharmacy.setBackgroundColor(getResources().getColor(R.color.background_light));
            btnPharmacy.setTextColor(getResources().getColor(R.color.text_primary));
        } else {
            btnPharmacy.setBackgroundColor(getResources().getColor(R.color.color_health));
            btnPharmacy.setTextColor(getResources().getColor(R.color.white));
            btnHospital.setBackgroundColor(getResources().getColor(R.color.background_light));
            btnHospital.setTextColor(getResources().getColor(R.color.text_primary));
        }
    }

    private void requestLocationAndSearch() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        } else {
            getLocationAndSearch();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationAndSearch();
            } else {
                Toast.makeText(this,
                        "Using default location: Istanbul",
                        Toast.LENGTH_SHORT).show();
                searchHealthFacilities(currentLat, currentLon, currentType);
            }
        }
    }

    private void getLocationAndSearch() {
        LocationManager locationManager =
                (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            Location location = locationManager
                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location == null) {
                location = locationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if (location != null) {
                currentLat = location.getLatitude();
                currentLon = location.getLongitude();
                Toast.makeText(this, "Location found!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,
                        "Using default location: Istanbul",
                        Toast.LENGTH_SHORT).show();
            }
        }
        searchHealthFacilities(currentLat, currentLon, currentType);
    }

    private void searchBySemt(String semt) {
        progressBar.setVisibility(View.VISIBLE);

        String url = "https://nominatim.openstreetmap.org/search?q=" +
                Uri.encode(semt + " istanbul") +
                "&format=json&limit=1&countrycodes=tr";

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("User-Agent", "AccessBridgeApp/1.0")
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, java.io.IOException e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(HealthActivity.this,
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response)
                    throws java.io.IOException {
                String responseBody = response.body().string();
                runOnUiThread(() -> {
                    try {
                        org.json.JSONArray results =
                                new org.json.JSONArray(responseBody);
                        if (results.length() > 0) {
                            org.json.JSONObject first = results.getJSONObject(0);
                            double lat = Double.parseDouble(first.getString("lat"));
                            double lon = Double.parseDouble(first.getString("lon"));
                            currentLat = lat;
                            currentLon = lon;
                            searchHealthFacilities(lat, lon, currentType);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(HealthActivity.this,
                                    "District not found",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private void searchHealthFacilities(double lat, double lon, String type) {
        progressBar.setVisibility(View.VISIBLE);
        itemList.clear();
        adapter.notifyDataSetChanged();

        int radius = 5000;

        String overpassQuery = "[out:json][timeout:60];" +
                "(" +
                "node[amenity=" + type + "](around:" + radius + "," + lat + "," + lon + ");" +
                "way[amenity=" + type + "](around:" + radius + "," + lat + "," + lon + ");" +
                ");" +
                "out center;";

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        RequestBody body = RequestBody.create(
                "data=" + Uri.encode(overpassQuery),
                MediaType.parse("application/x-www-form-urlencoded")
        );

        Request request = new Request.Builder()
                .url("https://overpass-api.de/api/interpreter")
                .post(body)
                .addHeader("User-Agent", "AccessBridgeApp/1.0")
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, java.io.IOException e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(HealthActivity.this,
                            "Connection error: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response)
                    throws java.io.IOException {

                if (!response.isSuccessful()) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(HealthActivity.this,
                                "Server error: " + response.code(),
                                Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                if (response.body() == null) {
                    runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                    return;
                }
                String responseBody = response.body().string();

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        org.json.JSONObject json =
                                new org.json.JSONObject(responseBody);
                        org.json.JSONArray elements =
                                json.getJSONArray("elements");

                        for (int i = 0; i < elements.length(); i++) {
                            org.json.JSONObject element =
                                    elements.getJSONObject(i);
                            if (element.has("tags")) {
                                org.json.JSONObject tags =
                                        element.getJSONObject("tags");
                                String name = tags.optString("name", "");
                                String phone = tags.optString("phone",
                                        "Not available");
                                String hours = tags.optString("opening_hours",
                                        "Not available");

                                org.json.JSONObject center = element.optJSONObject("center");
                                double elLat = element.optDouble("lat", center != null ? center.optDouble("lat", lat) : lat);
                                double elLon = element.optDouble("lon", center != null ? center.optDouble("lon", lon) : lon);

                                if (!name.isEmpty()) {
                                    String description =
                                            "📞 " + phone +
                                                    "\n🕐 " + hours +
                                                    "\n📍 " + String.format("%.4f", elLat) +
                                                    ", " + String.format("%.4f", elLon);
                                    itemList.add(new InfoItem(
                                            name, description, "health"));
                                }
                            }
                        }

                        adapter.notifyDataSetChanged();

                        if (itemList.isEmpty()) {
                            Toast.makeText(HealthActivity.this,
                                    "No " + type + "s found nearby",
                                    Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(HealthActivity.this,
                                "Error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
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