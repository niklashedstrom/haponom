package com.example.haponom;


import android.content.Intent;
import android.os.Bundle;

import android.view.View;

import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button p1_button = (Button) findViewById(R.id.BPM);
        p1_button.setText("140");

    }

    public void proximity(View view) {
        Intent intent = new Intent(this, proxTest.class);
        startActivity(intent);
    }

}
