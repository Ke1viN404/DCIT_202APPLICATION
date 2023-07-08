package com.monstertechno.timely;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button settings;
    ImageView nightModeImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(1);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_main);

        settings = findViewById(R.id.button2);
        nightModeImageView = findViewById(R.id.imageView);



        settings.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        nightModeImageView.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, FocusActivity.class);
            startActivity(intent);
        });
    }
}
