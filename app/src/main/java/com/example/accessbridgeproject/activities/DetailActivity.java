package com.example.accessbridgeproject.activities;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.accessbridgeproject.R;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import com.example.accessbridgeproject.utils.NetworkReceiver;
import android.content.Intent;

public class DetailActivity extends BaseActivity {
    private NetworkReceiver networkReceiver;
    private IntentFilter intentFilter;
    TextView tvTitle, tvDescription, tvToolbarTitle, btnBack;

    private com.example.accessbridgeproject.utils.TTSManager ttsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ttsManager = new com.example.accessbridgeproject.utils.TTSManager(this);
        com.example.accessbridgeproject.utils.SettingsManager settings = new com.example.accessbridgeproject.utils.SettingsManager(this);

        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvToolbarTitle = findViewById(R.id.tvToolbarTitle);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        Intent intent = getIntent();
        if (intent != null) {
            String title = intent.getStringExtra("title");
            String description = intent.getStringExtra("description");
            String category = intent.getStringExtra("category");

            tvTitle.setText(title);
            tvDescription.setText(description);
            tvToolbarTitle.setText(title);

            if (settings.isVoiceReaderEnabled()) {
                // Read out loud with slight delay to ensure TTS engine is loaded
                tvTitle.postDelayed(() -> {
                    ttsManager.speak(title + ". " + description);
                }, 1000);
            }
        }
        
        networkReceiver = new NetworkReceiver();
        intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
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
