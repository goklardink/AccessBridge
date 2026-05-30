package com.example.accessbridgeproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import com.example.accessbridgeproject.R;
import com.example.accessbridgeproject.utils.SettingsManager;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SettingsManager settings = new SettingsManager(this);

        RadioGroup rg = findViewById(R.id.radioGroupFontSize);
        Switch swContrast = findViewById(R.id.switchHighContrast);
        Switch swSimple = findViewById(R.id.switchSimpleLanguage);
        Switch swVoice = findViewById(R.id.switchVoiceReader);
        Button btnBack = findViewById(R.id.btnBack);

        float currentScale = settings.getFontScale();
        if (currentScale == 1.0f) {
            rg.check(R.id.radioNormal);
        } else if (currentScale == 1.3f) {
            rg.check(R.id.radioLarge);
        } else if (currentScale == 1.5f) {
            rg.check(R.id.radioHuge);
        }

        swContrast.setChecked(settings.isHighContrast());
        swSimple.setChecked(settings.isSimpleLanguage());
        swVoice.setChecked(settings.isVoiceReaderEnabled());

        rg.setOnCheckedChangeListener((group, checkedId) -> {
            float scale = 1.0f;
            if (checkedId == R.id.radioLarge) scale = 1.3f;
            else if (checkedId == R.id.radioHuge) scale = 1.5f;
            settings.setFontScale(scale);
            restartApp();
        });

        swContrast.setOnCheckedChangeListener((buttonView, isChecked) -> {
            settings.setHighContrast(isChecked);
            restartApp();
        });

        swSimple.setOnCheckedChangeListener((buttonView, isChecked) -> {
            settings.setSimpleLanguage(isChecked);
            restartApp();
        });

        swVoice.setOnCheckedChangeListener((buttonView, isChecked) -> {
            settings.setVoiceReaderEnabled(isChecked);
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void restartApp() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
