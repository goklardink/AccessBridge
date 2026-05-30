package com.example.accessbridgeproject.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import com.example.accessbridgeproject.R;
import com.example.accessbridgeproject.utils.NetworkReceiver;

public class MainActivity extends BaseActivity {

    CardView cardHealth, cardLegal, cardTransport, cardEducation;
    private static final String CHANNEL_ID = "accessbridge_channel";
    private NetworkReceiver networkReceiver;
    private IntentFilter intentFilter;
    private com.example.accessbridgeproject.utils.TTSManager ttsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
        }
        setContentView(R.layout.activity_main);

        createNotificationChannel();
        showWelcomeNotification();

        cardHealth = findViewById(R.id.cardHealth);
        cardLegal = findViewById(R.id.cardLegal);
        cardTransport = findViewById(R.id.cardTransport);
        cardEducation = findViewById(R.id.cardEducation);
        android.widget.ImageView btnSettings = findViewById(R.id.btnSettings);

        btnSettings.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SettingsActivity.class)));

        cardHealth.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HealthActivity.class);
            startActivity(intent);
        });

        cardLegal.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LegalActivity.class);
            startActivity(intent);
        });

        cardTransport.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TransportActivity.class);
            startActivity(intent);
        });

        cardEducation.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EducationActivity.class);
            startActivity(intent);
        });

        com.example.accessbridgeproject.utils.SettingsManager settings = new com.example.accessbridgeproject.utils.SettingsManager(this);
        ttsManager = new com.example.accessbridgeproject.utils.TTSManager(this);

        cardHealth.setOnLongClickListener(v -> {
            if (settings.isVoiceReaderEnabled()) {
                ttsManager.speak(getString(R.string.health) + ". " + getString(R.string.health_desc));
            }
            return true;
        });

        cardLegal.setOnLongClickListener(v -> {
            if (settings.isVoiceReaderEnabled()) {
                ttsManager.speak(getString(R.string.legal) + ". " + getString(R.string.legal_desc));
            }
            return true;
        });

        cardTransport.setOnLongClickListener(v -> {
            if (settings.isVoiceReaderEnabled()) {
                ttsManager.speak(getString(R.string.transport) + ". " + getString(R.string.transport_desc));
            }
            return true;
        });

        cardEducation.setOnLongClickListener(v -> {
            if (settings.isVoiceReaderEnabled()) {
                ttsManager.speak(getString(R.string.education) + ". " + getString(R.string.education_desc));
            }
            return true;
        });

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ttsManager != null) {
            ttsManager.shutdown();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "AccessBridge Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("AccessBridge app notifications");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void showWelcomeNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Welcome to AccessBridge!")
                .setContentText("Access health, legal, transport and education information.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_about) {
            Toast.makeText(this,
                    "AccessBridge v1.0 - Information platform for everyone",
                    Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}