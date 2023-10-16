package com.example.atarigame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class MainActivityTwo extends AppCompatActivity {
    TextView score;
    TextView hit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_two);
        score = findViewById(R.id.id_score);
        hit = findViewById(R.id.id_hit);

    }
}