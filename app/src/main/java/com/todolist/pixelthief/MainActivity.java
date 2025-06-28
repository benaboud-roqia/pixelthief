package com.todolist.pixelthief;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private GameGridView gameGridView;
    private TextView tvStatus;
    private TextView tvScore;
    private Button btnUseItem;
    private static int currentLevel = 1;
    public static final int MAX_LEVELS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        gameGridView = findViewById(R.id.gameGridView);
        tvStatus = findViewById(R.id.tvStatus);
        tvScore = findViewById(R.id.tvScore);
        btnUseItem = findViewById(R.id.btnUseItem);

        Button btnUp = findViewById(R.id.btnUp);
        Button btnDown = findViewById(R.id.btnDown);
        Button btnLeft = findViewById(R.id.btnLeft);
        Button btnRight = findViewById(R.id.btnRight);

        View.OnClickListener moveListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gameGridView.getGameGrid().hasWon()) {
                    SoundManager.getInstance(MainActivity.this).playSound(SoundManager.Sound.VICTORY);
                    if (currentLevel < MAX_LEVELS) {
                        currentLevel++;
                        Toast.makeText(MainActivity.this, "Niveau " + currentLevel, Toast.LENGTH_SHORT).show();
                        startGame(currentLevel);
                    } else {
                        Intent intent = new Intent(MainActivity.this, VictoryActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                    return;
                }
                if (gameGridView.getGameGrid().hasLost()) {
                    SoundManager.getInstance(MainActivity.this).playSound(SoundManager.Sound.GAME_OVER);
                    Intent intent = new Intent(MainActivity.this, GameOverActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    return;
                }
                int oldScore = gameGridView.getGameGrid().getScore();
                int dx = 0, dy = 0;
                if (v.getId() == R.id.btnUp) dy = -1;
                else if (v.getId() == R.id.btnDown) dy = 1;
                else if (v.getId() == R.id.btnLeft) dx = -1;
                else if (v.getId() == R.id.btnRight) dx = 1;
                gameGridView.getGameGrid().movePlayer(dx, dy);
                if (gameGridView.getGameGrid().getScore() > oldScore) {
                    SoundManager.getInstance(MainActivity.this).playSound(SoundManager.Sound.COLLECT_GEM);
                } else if (dx != 0 || dy != 0) {
                    SoundManager.getInstance(MainActivity.this).playSound(SoundManager.Sound.PLAYER_MOVE);
                }
                gameGridView.invalidate();
                updateStatus();
            }
        };
        btnUp.setOnClickListener(moveListener);
        btnDown.setOnClickListener(moveListener);
        btnLeft.setOnClickListener(moveListener);
        btnRight.setOnClickListener(moveListener);

        btnUseItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameGridView.getGameGrid().useFirstItem();
                SoundManager.getInstance(MainActivity.this).playSound(SoundManager.Sound.USE_ITEM);
                gameGridView.invalidate();
                updateStatus();
            }
        });

        startGame(currentLevel);
    }

    private void startGame(int level) {
        gameGridView.getGameGrid().loadLevel(level);
        gameGridView.invalidate();
        updateStatus();
    }

    private void updateStatus() {
        GameGrid grid = gameGridView.getGameGrid();

        // Mettre à jour le score
        tvScore.setText("Score: " + grid.getScore());

        // Afficher le statut d'invisibilité
        if (grid.isPlayerInvisible()) {
            tvStatus.setText("Invisible pour " + grid.getInvisibilityTurnsLeft() + " tours");
        } else {
            // Afficher l'inventaire
            if (grid.getPlayerInventory().isEmpty()) {
                tvStatus.setText("Inventaire vide");
            } else {
                tvStatus.setText("Inventaire: " + grid.getPlayerInventory().get(0));
            }
        }

        // Mettre à jour le bouton "Utiliser"
        btnUseItem.setVisibility(
                !grid.getPlayerInventory().isEmpty() && !grid.isPlayerInvisible()
                ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        currentLevel = 1; // Reset level when going back to menu
    }
}