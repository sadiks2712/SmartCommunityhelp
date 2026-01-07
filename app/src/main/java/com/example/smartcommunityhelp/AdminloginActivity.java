package com.example.smartcommunityhelp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class AdminloginActivity extends AppCompatActivity {

    Button b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.adminlogin);

        // Initialize button
        b = findViewById(R.id.ad);

        // Button click action
        b.setOnClickListener(v -> {
            Intent intent = new Intent(AdminloginActivity.this,AdminActivity.class);
            startActivity(intent);
        });
    }
}
