package com.example.recruitmenttask.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.recruitmenttask.R;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initComponents();
    }

    private void initComponents() {
        FrameLayout bkashBtn = (FrameLayout) findViewById(R.id.bKash_btn);
        bkashBtn.setOnClickListener(v -> {
            Intent bKash = new Intent(this, BkashPaymentActivity.class);
            startActivity(bKash);
        });

        FrameLayout nagadBtn = (FrameLayout) findViewById(R.id.nagad_btn);
        nagadBtn.setOnClickListener(v -> {
            Intent nagad = new Intent(this, NagadPaymentActivity.class);
            startActivity(nagad);
        });
    }
}