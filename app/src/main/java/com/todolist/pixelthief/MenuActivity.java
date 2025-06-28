package com.todolist.pixelthief;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        SoundManager.getInstance(this).startMusic();

        Button btnPlay = findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SoundManager.getInstance(this).startMusic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Optionnel: mettre la musique en pause si on quitte le jeu
        // SoundManager.getInstance(this).pauseMusic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Optionnel: si vous voulez arrêter la musique complètement
        // SoundManager.getInstance(this).release();
    }
} 