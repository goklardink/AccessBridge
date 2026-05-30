package com.example.accessbridgeproject.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsManager {
    private static final String PREF_NAME = "AccessibilitySettings";
    private static final String KEY_FONT_SCALE = "font_scale";
    private static final String KEY_HIGH_CONTRAST = "high_contrast";
    private static final String KEY_SIMPLE_LANGUAGE = "simple_language";
    private static final String KEY_VOICE_READER = "voice_reader";

    private SharedPreferences prefs;

    public SettingsManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public float getFontScale() {
        return prefs.getFloat(KEY_FONT_SCALE, 1.0f);
    }

    public void setFontScale(float scale) {
        prefs.edit().putFloat(KEY_FONT_SCALE, scale).apply();
    }

    public boolean isHighContrast() {
        return prefs.getBoolean(KEY_HIGH_CONTRAST, false);
    }

    public void setHighContrast(boolean enabled) {
        prefs.edit().putBoolean(KEY_HIGH_CONTRAST, enabled).apply();
    }

    public boolean isSimpleLanguage() {
        return prefs.getBoolean(KEY_SIMPLE_LANGUAGE, false);
    }

    public void setSimpleLanguage(boolean enabled) {
        prefs.edit().putBoolean(KEY_SIMPLE_LANGUAGE, enabled).apply();
    }

    public boolean isVoiceReaderEnabled() {
        return prefs.getBoolean(KEY_VOICE_READER, false);
    }

    public void setVoiceReaderEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_VOICE_READER, enabled).apply();
    }
}
