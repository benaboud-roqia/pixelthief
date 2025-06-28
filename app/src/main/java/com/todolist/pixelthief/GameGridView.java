package com.todolist.pixelthief;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import androidx.core.content.ContextCompat;
import java.util.List;

public class GameGridView extends View {
    private GameGrid gameGrid;
    private Paint fogPaint, seenPaint, cameraVisionPaint;

    // Drawables pour chaque type d'entité et de tuile
    private Drawable floorDrawable, wallDrawable, noisyTileDrawable;
    private Drawable playerDrawable, guardDrawable, lootDrawable, exitDrawable;
    private Drawable cameraDrawable, gemDrawable, pressurePlateDrawable;
    private Drawable hidingSpotDrawable;

    public GameGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        gameGrid = new GameGrid(10, 10); // Taille par défaut
        init(context);
    }

    private void init(Context context) {
        // Initialiser les peintures
        fogPaint = new Paint();
        fogPaint.setColor(Color.BLACK);

        seenPaint = new Paint();
        seenPaint.setColor(Color.argb(180, 10, 5, 20)); // Un voile sombre et violacé

        cameraVisionPaint = new Paint();
        cameraVisionPaint.setColor(Color.argb(80, 255, 255, 0)); // Cône de vision jaune semi-transparent

        // Charger tous les drawables depuis les ressources XML
        floorDrawable = ContextCompat.getDrawable(context, R.drawable.tile_floor);
        wallDrawable = ContextCompat.getDrawable(context, R.drawable.tile_wall);
        playerDrawable = ContextCompat.getDrawable(context, R.drawable.entity_player);
        guardDrawable = ContextCompat.getDrawable(context, R.drawable.entity_guard);
        lootDrawable = ContextCompat.getDrawable(context, R.drawable.entity_loot);
        exitDrawable = ContextCompat.getDrawable(context, R.drawable.entity_exit);
        cameraDrawable = ContextCompat.getDrawable(context, R.drawable.entity_camera);
        gemDrawable = ContextCompat.getDrawable(context, R.drawable.entity_gem);
        hidingSpotDrawable = ContextCompat.getDrawable(context, R.drawable.tile_hiding_spot);

        // Créez des drawables pour les autres tuiles si nécessaire.
        // Pour l'instant, nous les dessinons avec des couleurs.
        noisyTileDrawable = ContextCompat.getDrawable(context, R.drawable.tile_floor); // Réutiliser le sol pour l'instant
        pressurePlateDrawable = ContextCompat.getDrawable(context, R.drawable.tile_floor); // idem
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (gameGrid == null || floorDrawable == null) return;

        int canvasWidth = getWidth();
        int canvasHeight = getHeight();
        int cols = gameGrid.getWidth();
        int rows = gameGrid.getHeight();
        int tileSize = Math.min(canvasWidth / cols, canvasHeight / rows);
        
        int offsetX = (canvasWidth - cols * tileSize) / 2;
        int offsetY = (canvasHeight - rows * tileSize) / 2;

        canvas.drawColor(Color.BLACK); // Fond noir

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                int left = offsetX + x * tileSize;
                int top = offsetY + y * tileSize;
                int right = left + tileSize;
                int bottom = top + tileSize;

                GameGrid.Tile tile = gameGrid.getTile(x, y);

                // 1. Gérer la visibilité
                if (tile.visibility == GameGrid.Visibility.NOT_VISIBLE) {
                    canvas.drawRect(left, top, right, bottom, fogPaint);
                    continue; // Ne rien dessiner d'autre
                }

                // 2. Dessiner la base de la tuile
                Drawable baseDrawable = floorDrawable;
                if (tile.type == GameGrid.TileType.WALL) baseDrawable = wallDrawable;
                else if (tile.type == GameGrid.TileType.HIDING_SPOT) baseDrawable = hidingSpotDrawable;
                // Ajoutez ici d'autres types de sol si nécessaire
                
                baseDrawable.setBounds(left, top, right, bottom);
                baseDrawable.draw(canvas);

                // 3. Dessiner les éléments sur la tuile (butin, gemme, etc.)
                Drawable entityDrawable = null;
                switch (tile.type) {
                    case LOOT: entityDrawable = lootDrawable; break;
                    case EXIT: entityDrawable = exitDrawable; break;
                    case CAMERA: entityDrawable = cameraDrawable; break;
                    case GEM: entityDrawable = gemDrawable; break;
                    // Ajoutez ici d'autres types
                }

                if (entityDrawable != null) {
                    entityDrawable.setBounds(left, top, right, bottom);
                    entityDrawable.draw(canvas);
                }
                
                // 4. Dessiner les objets (items) sur la tuile
                if (tile.itemOnTile != null) {
                    // Pour l'instant, on dessine un cercle. Idéalement, utilisez des drawables.
                    Paint itemPaint = new Paint();
                    itemPaint.setStyle(Paint.Style.FILL);
                    int itemColor = Color.WHITE;
                    switch (tile.itemOnTile) {
                        case SMOKE_BOMB: itemColor = Color.GRAY; break;
                        case EMP_CHIP: itemColor = Color.CYAN; break;
                        case FAKE_FOOTPRINTS: itemColor = Color.YELLOW; break;
                    }
                    itemPaint.setColor(itemColor);
                    canvas.drawCircle(left + tileSize / 2, top + tileSize / 2, tileSize / 4, itemPaint);
                }

                // 5. Dessiner la vision de la caméra
                if (!gameGrid.isCameraDisabled()) {
                    List<int[]> vision = gameGrid.getCameraVision();
                    for (int[] pos : vision) {
                        if (pos[0] == x && pos[1] == y) {
                            canvas.drawRect(left, top, right, bottom, cameraVisionPaint);
                        }
                    }
                }
                
                // 6. Dessiner les entités mobiles (joueur, garde)
                if (tile.hasGuard) {
                    guardDrawable.setBounds(left, top, right, bottom);
                    guardDrawable.draw(canvas);
                }
                if (tile.hasPlayer) {
                    playerDrawable.setBounds(left, top, right, bottom);
                    // Appliquer un effet de transparence si caché ou invisible
                    if (gameGrid.isPlayerHidden() || gameGrid.isPlayerInvisible()) {
                        playerDrawable.setAlpha(128); // 50% transparent
                    } else {
                        playerDrawable.setAlpha(255); // Opaque
                    }
                    playerDrawable.draw(canvas);
                    if (gameGrid.isPlayerInvisible()) {
                        Paint invisiblePaint = new Paint();
                        invisiblePaint.setColor(Color.argb(128, 255, 255, 255));
                        canvas.drawRect(left, top, right, bottom, invisiblePaint);
                    }
                }
                
                // 7. Appliquer le voile "déjà vu" par-dessus tout
                if (tile.visibility == GameGrid.Visibility.SEEN) {
                    canvas.drawRect(left, top, right, bottom, seenPaint);
                }
            }
        }
    }

    public GameGrid getGameGrid() {
        return gameGrid;
    }
}
