package com.example.accessbridgeproject.activities;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.accessbridgeproject.R;
import com.example.accessbridgeproject.utils.SettingsManager;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!(this instanceof SplashActivity)) {
            SettingsManager settings = new SettingsManager(this);
            if (settings.isHighContrast()) {
                setTheme(R.style.Theme_HighContrast);
            } else {
                setTheme(R.style.Theme_AccessBridge);
            }
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        SettingsManager settings = new SettingsManager(newBase);
        Configuration configuration = new Configuration(newBase.getResources().getConfiguration());
        configuration.fontScale = settings.getFontScale();
        Context context = newBase.createConfigurationContext(configuration);
        super.attachBaseContext(context);
    }
}
