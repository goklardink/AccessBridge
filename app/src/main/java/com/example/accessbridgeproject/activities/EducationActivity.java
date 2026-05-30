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

public class EducationActivity extends BaseActivity {
    private NetworkReceiver networkReceiver;
    private IntentFilter intentFilter;
    RecyclerView recyclerView;
    InfoAdapter adapter;
    List<InfoItem> itemList;
    TextView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education);

        btnBack = findViewById(R.id.btnBack);
        recyclerView = findViewById(R.id.recyclerViewEducation);

        btnBack.setOnClickListener(v -> finish());

        itemList = new ArrayList<>();
        loadEducationResources();

        adapter = new InfoAdapter(this, itemList, item -> {
            Intent intent = new Intent(EducationActivity.this, DetailActivity.class);
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
    private void loadEducationResources() {
        itemList.add(new InfoItem(
                "Free Online Courses",
                "Access thousands of free courses on Coursera, edX, and Khan Academy. Learn new skills at your own pace from anywhere.",
                "education"
        ));
        itemList.add(new InfoItem(
                "Scholarship Opportunities",
                "Many organizations offer scholarships for disadvantaged individuals. Check government websites and NGOs for available funding.",
                "education"
        ));
        itemList.add(new InfoItem(
                "Adult Education Programs",
                "Free adult education programs are available at local community centers. Learn to read, write, and gain basic skills.",
                "education"
        ));
        itemList.add(new InfoItem(
                "Language Learning Resources",
                "Free language learning apps like Duolingo and government language courses can help you integrate better.",
                "education"
        ));
        itemList.add(new InfoItem(
                "Vocational Training",
                "Government-funded vocational training programs help you gain practical skills for employment.",
                "education"
        ));
        itemList.add(new InfoItem(
                "Children Education Support",
                "Free tutoring and after-school programs are available for children who need extra academic support.",
                "education"
        ));
    }
}