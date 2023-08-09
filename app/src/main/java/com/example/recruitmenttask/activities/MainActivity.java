package com.example.recruitmenttask.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.recruitmenttask.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();
    }

    private void initComponents() {
        Button goBtn = (Button) findViewById(R.id.go_btn);
        goBtn.setOnClickListener(v -> {
            Intent dashboard = new Intent(this, DashboardActivity.class);
            startActivity(dashboard);
        });
    }
}