package com.example.accessbridgeproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.accessbridgeproject.R;
import com.example.accessbridgeproject.adapters.InfoAdapter;
import com.example.accessbridgeproject.models.InfoItem;
import java.util.ArrayList;
import java.util.List;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import com.example.accessbridgeproject.utils.NetworkReceiver;

public class LegalActivity extends BaseActivity {
    private NetworkReceiver networkReceiver;
    private IntentFilter intentFilter;
    RecyclerView recyclerView;
    InfoAdapter adapter;
    List<InfoItem> itemList;
    TextView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legal);

        btnBack = findViewById(R.id.btnBack);
        recyclerView = findViewById(R.id.recyclerViewLegal);

        btnBack.setOnClickListener(v -> finish());

        itemList = new ArrayList<>();
        loadLegalRights();

        adapter = new InfoAdapter(this, itemList, item -> {
            Intent intent = new Intent(LegalActivity.this, DetailActivity.class);
            intent.putExtra("title", item.getTitle());
            intent.putExtra("description", item.getDescription());
            intent.putExtra("category", item.getCategory());
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
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
    private void loadLegalRights() {
        com.example.accessbridgeproject.utils.SettingsManager settings = new com.example.accessbridgeproject.utils.SettingsManager(this);
        boolean isSimple = settings.isSimpleLanguage();

        itemList.add(new InfoItem(
                getString(R.string.right_equality_title),
                isSimple ? getString(R.string.right_equality_desc_simple) : getString(R.string.right_equality_desc),
                "legal"
        ));
        itemList.add(new InfoItem(
                getString(R.string.right_disability_title),
                isSimple ? getString(R.string.right_disability_desc_simple) : getString(R.string.right_disability_desc),
                "legal"
        ));
        itemList.add(new InfoItem(
                getString(R.string.right_health_title),
                isSimple ? getString(R.string.right_health_desc_simple) : getString(R.string.right_health_desc),
                "legal"
        ));
        itemList.add(new InfoItem(
                getString(R.string.right_privacy_title),
                isSimple ? getString(R.string.right_privacy_desc_simple) : getString(R.string.right_privacy_desc),
                "legal"
        ));
        itemList.add(new InfoItem(
                getString(R.string.right_work_title),
                isSimple ? getString(R.string.right_work_desc_simple) : getString(R.string.right_work_desc),
                "legal"
        ));
        itemList.add(new InfoItem(
                getString(R.string.right_vote_title),
                isSimple ? getString(R.string.right_vote_desc_simple) : getString(R.string.right_vote_desc),
                "legal"
        ));
    }
}