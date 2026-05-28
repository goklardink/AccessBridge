package com.example.accessbridgeproject.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NetworkReceiver extends BroadcastReceiver {

    private static boolean wasConnected = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        boolean isConnected = networkInfo != null && networkInfo.isConnected();

        if (context instanceof Activity) {
            Activity activity = (Activity) context;

            if (!isConnected && wasConnected) {
                showDialog(activity, false);
            } else if (isConnected && !wasConnected) {
                showDialog(activity, true);
            }

            wasConnected = isConnected;
        }
    }

    private void showDialog(Activity activity, boolean isConnected) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 60, 60, 40);

        if (isConnected) {
            layout.setBackgroundColor(Color.parseColor("#1B5E20"));
        } else {
            layout.setBackgroundColor(Color.parseColor("#B71C1C"));
        }

        TextView tvIcon = new TextView(activity);
        tvIcon.setTextSize(48);
        tvIcon.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        if (isConnected) {
            tvIcon.setText("✅");
        } else {
            tvIcon.setText("⚠️");
        }
        layout.addView(tvIcon);

        TextView tvTitle = new TextView(activity);
        tvTitle.setTextSize(24);
        tvTitle.setTypeface(null, Typeface.BOLD);
        tvTitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tvTitle.setPadding(0, 16, 0, 8);
        tvTitle.setTextColor(Color.parseColor("#FFFFFF"));
        if (isConnected) {
            tvTitle.setText("Connected!");
        } else {
            tvTitle.setText("No Internet Connection!");
        }
        layout.addView(tvTitle);

        TextView tvMessage = new TextView(activity);
        tvMessage.setTextSize(16);
        tvMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tvMessage.setPadding(0, 0, 0, 24);
        tvMessage.setTextColor(Color.parseColor("#FFFFFF"));
        if (isConnected) {
            tvMessage.setText("Your internet connection has been restored.");
        } else {
            tvMessage.setText("Health and Transport features require internet.\nLegal and Education work offline.");
        }
        layout.addView(tvMessage);

        Button btnOk = new Button(activity);
        btnOk.setText("OK");
        btnOk.setTextSize(18);
        btnOk.setTypeface(null, Typeface.BOLD);
        if (isConnected) {
            btnOk.setTextColor(Color.parseColor("#1B5E20"));
            btnOk.setBackgroundColor(Color.parseColor("#FFFFFF"));
        } else {
            btnOk.setTextColor(Color.parseColor("#B71C1C"));
            btnOk.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        builder.setView(layout);
        AlertDialog dialog = builder.create();

        btnOk.setOnClickListener(v -> dialog.dismiss());
        layout.addView(btnOk);

        if (!activity.isFinishing()) {
            dialog.show();
        }
    }
}