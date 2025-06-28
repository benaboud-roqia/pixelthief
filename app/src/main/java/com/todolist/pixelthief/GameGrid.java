package com.todolist.pixelthief;

import java.util.ArrayList;
import java.util.List;

// Classe représentant une case de la grille
public class GameGrid {
    public enum TileType {
        EMPTY, WALL, NOISY, SILENT, PRESSURE_PLATE, CAMERA, LOOT, EXIT, GEM, HIDING_SPOT
    }

    public enum ItemType {
        SMOKE_BOMB, EMP_CHIP, FAKE_FOOTPRINTS
    }

    public enum Visibility {
        NOT_VISIBLE, VISIBLE, SEEN
    }

    public static class Tile {
        public TileType type;
        public ItemType itemOnTile = null;
        public Visibility visibility = Visibility.NOT_VISIBLE;
        public boolean hasPlayer;
        public boolean hasGuard;
        public boolean hasItem;

        public Tile(TileType type) {
            this.type = type;
            this.hasPlayer = false;
            this.hasGuard = false;
            this.hasItem = false;
        }
    }

    private int width;
    private int height;
    private Tile[][] grid;
    private int playerX, playerY;
    private boolean lootCollected = false;
    private boolean hasWon = false;
    private int score = 0;
    private boolean isPlayerHidden = false;
    private int guardX = 5;
    private int guardY = 1;
    private int guardDir = 1; // 1 = droite, -1 = gauche
    private boolean hasLost = false;
    private int cameraX = 2;
    private int cameraY = 4;
    private int cameraVisionRange = 3;
    private List<int[]> cameraVisionTiles = new ArrayList<>();
    private List<ItemType> playerInventory = new ArrayList<>();
    private boolean isPlayerInvisible = false;
    private int invisibilityTurnsLeft = 0;
    private boolean isCameraDisabled = false;
    private int cameraDisabledTurnsLeft = 0;
    private int fakeFootprintX = -1, fakeFootprintY = -1;
    private int fakeFootprintTurnsLeft = 0;
    private int visionRange = 2; // Le joueur voit à 2 cases autour de lui

    public GameGrid(int width, int height) {
        this.width = width;
        this.height = height;
        grid = new Tile[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = new Tile(TileType.EMPTY);
            }
        }
        // Position initiale du joueur
        playerX = 1;
        playerY = 1;
        grid[playerY][playerX].hasPlayer = true;
        loadLevel(1);
    }

    public Tile getTile(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) return null;
        return grid[y][x];
    }

    public boolean movePlayer(int dx, int dy) {
        int newX = playerX + dx;
        int newY = playerY + dy;
        if (newX < 0 || newY < 0 || newX >= width || newY >= height) return false;
        Tile dest = grid[newY][newX];
        if (dest.type == TileType.WALL) return false;
        grid[playerY][playerX].hasPlayer = false;
        playerX = newX;
        playerY = newY;
        grid[playerY][playerX].hasPlayer = true;
        // Mise à jour de l'état "caché"
        isPlayerHidden = (dest.type == TileType.HIDING_SPOT);
        // Gestion du ramassage d'objet
        if (grid[newY][newX].itemOnTile != null) {
            playerInventory.add(grid[newY][newX].itemOnTile);
            grid[newY][newX].itemOnTile = null;
        }
        // Gestion du ramassage de gemme
        if (dest.type == TileType.GEM) {
            score += 100; // Ajoute 100 points par gemme
            dest.type = TileType.EMPTY;
        }
        // Gestion du loot
        if (dest.type == TileType.LOOT && !lootCollected) {
            lootCollected = true;
            dest.type = TileType.EMPTY;
        }
        // Gestion de la défaite par plaque de pression
        if (dest.type == TileType.PRESSURE_PLATE) {
            hasLost = true;
        }
        // Gestion de la victoire
        if (dest.type == TileType.EXIT && lootCollected) {
            hasWon = true;
        }
        // Déplacement du garde
        moveGuard(newX, newY);
        // Vérification défaite par garde
        if (!isPlayerInvisible && !isPlayerHidden && playerX == guardX && playerY == guardY) {
            hasLost = true;
        }
        // Vérification défaite par caméra
        updateCameraVision();
        checkCameraDetection(newX, newY);

        // Mettre à jour l'invisibilité
        if (isPlayerInvisible) {
            invisibilityTurnsLeft--;
            if (invisibilityTurnsLeft <= 0) {
                isPlayerInvisible = false;
            }
        }

        // Mettre à jour la caméra désactivée
        if (isCameraDisabled) {
            cameraDisabledTurnsLeft--;
            if (cameraDisabledTurnsLeft <= 0) {
                isCameraDisabled = false;
            }
        }

        // Mettre à jour les fausses empreintes
        if (fakeFootprintTurnsLeft > 0) {
            fakeFootprintTurnsLeft--;
            if (fakeFootprintTurnsLeft <= 0) {
                fakeFootprintX = -1;
                fakeFootprintY = -1;
            }
        }

        // Mettre à jour la visibilité
        updateVisibility();
        return true;
    }

    private void moveGuard(int playerX, int playerY) {
        // Enlève le garde de l'ancienne case
        grid[guardY][guardX].hasGuard = false;

        // Le garde est-il attiré par des empreintes ? (Priorité 1)
        if (fakeFootprintX != -1) {
            if (fakeFootprintX > guardX) guardX++;
            else if (fakeFootprintX < guardX) guardX--;
            else if (fakeFootprintY > guardY) guardY++;
            else if (fakeFootprintY < guardY) guardY--;
            // Si le garde atteint l'empreinte, elle disparaît
            if (guardX == fakeFootprintX && guardY == fakeFootprintY) {
                fakeFootprintX = -1;
                fakeFootprintY = -1;
                fakeFootprintTurnsLeft = 0;
            }
        }
        // Gestion de l'alerte par tuile bruyante (Priorité 2)
        else if (!isPlayerInvisible && !isPlayerHidden && grid[playerY][playerX].type == TileType.NOISY) {
            // Le garde se dirige vers le joueur
            if (playerX > guardX) guardX++;
            else if (playerX < guardX) guardX--;
            else if (playerY > guardY) guardY++;
            else if (playerY < guardY) guardY--;
        } else {
            // Patrouille normale
            int nextX = guardX + guardDir;
            if (nextX <= 0 || nextX >= width - 1 || grid[guardY][nextX].type == TileType.WALL) {
                guardDir *= -1;
                nextX = guardX + guardDir;
            }
            guardX = nextX;
        }

        grid[guardY][guardX].hasGuard = true;
    }

    private void checkCameraDetection(int playerX, int playerY) {
        if (isPlayerInvisible || isCameraDisabled || isPlayerHidden) return; // Pas de détection si caché

        for (int[] pos : cameraVisionTiles) {
            if (playerX == pos[0] && playerY == pos[1]) {
                hasLost = true;
                return;
            }
        }
    }

    private void updateCameraVision() {
        cameraVisionTiles.clear();
        for (int i = 1; i <= cameraVisionRange; i++) {
            int tx = cameraX;
            int ty = cameraY + i; // vers le bas
            if (tx >= 0 && ty >= 0 && tx < width && ty < height && grid[ty][tx].type != TileType.WALL) {
                cameraVisionTiles.add(new int[]{tx, ty});
            } else {
                break;
            }
        }
    }

    public List<int[]> getCameraVision() {
        updateCameraVision();
        return cameraVisionTiles;
    }

    public int getPlayerX() { return playerX; }
    public int getPlayerY() { return playerY; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getGuardX() { return guardX; }
    public int getGuardY() { return guardY; }

    // Méthode pour placer un élément sur la grille
    public void setTile(int x, int y, TileType type) {
        if (x < 0 || y < 0 || x >= width || y >= height) return;
        grid[y][x].type = type;
    }

    private void updateVisibility() {
        // 1. Les cases actuellement visibles deviennent "déjà vues"
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (grid[y][x].visibility == Visibility.VISIBLE) {
                    grid[y][x].visibility = Visibility.SEEN;
                }
            }
        }

        // 2. Les nouvelles cases autour du joueur deviennent visibles
        for (int y = playerY - visionRange; y <= playerY + visionRange; y++) {
            for (int x = playerX - visionRange; x <= playerX + visionRange; x++) {
                if (x >= 0 && y >= 0 && x < width && y < height) {
                    // Simple distance carrée pour l'instant
                    grid[y][x].visibility = Visibility.VISIBLE;
                }
            }
        }
    }

    public void loadLevel(int level) {
        // Reset a zero de l'etat du jeu
        lootCollected = false;
        hasWon = false;
        hasLost = false;
        score = 0;
        isPlayerHidden = false;
        playerInventory.clear();
        isPlayerInvisible = false;
        invisibilityTurnsLeft = 0;
        isCameraDisabled = false;
        cameraDisabledTurnsLeft = 0;
        fakeFootprintX = -1;
        fakeFootprintY = -1;
        fakeFootprintTurnsLeft = 0;

        // Reset la grille
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = new Tile(TileType.EMPTY);
            }
        }
        if (level == 1) {
            generateLevel1();
        } else if (level == 2) {
            generateLevel2();
        }

        grid[playerY][playerX].hasPlayer = true;
        grid[guardY][guardX].hasGuard = true;

        updateVisibility();
        updateCameraVision();
    }

    private void generateLevel1() {
        // Position initiale du joueur
        playerX = 1;
        playerY = 1;

        // Position du garde
        guardX = 5;
        guardY = 1;
        guardDir = 1;

        // Murs
        for (int i = 0; i < width; i++) {
            setTile(i, 0, TileType.WALL);
            setTile(i, height - 1, TileType.WALL);
        }
        for (int i = 0; i < height; i++) {
            setTile(0, i, TileType.WALL);
            setTile(width - 1, i, TileType.WALL);
        }

        setTile(3, 1, TileType.WALL);
        setTile(3, 2, TileType.WALL);
        setTile(3, 3, TileType.WALL);
        setTile(5, 3, TileType.WALL);
        setTile(5, 4, TileType.WALL);
        setTile(5, 5, TileType.WALL);


        // Items et autres
        setTile(cameraX, cameraY, TileType.CAMERA);
        setTile(7, 3, TileType.LOOT);
        setTile(1, 7, TileType.EXIT);
        setTile(4, 2, TileType.NOISY);
        setTile(2, 5, TileType.PRESSURE_PLATE);

        setTile(1, 4, TileType.GEM);
        setTile(6, 1, TileType.GEM);

        setTile(4, 3, TileType.HIDING_SPOT);

        grid[1][5].itemOnTile = ItemType.SMOKE_BOMB;
        grid[6][6].itemOnTile = ItemType.EMP_CHIP;
    }

    private void generateLevel2() {
        // Niveau 2 - plus difficile
        playerX = 1;
        playerY = 1;

        // Garde
        guardX = 7;
        guardY = 1;
        guardDir = -1;

        // Murs
        for (int i = 0; i < width; i++) {
            setTile(i, 0, TileType.WALL);
            setTile(i, height - 1, TileType.WALL);
        }
        for (int i = 0; i < height; i++) {
            setTile(0, i, TileType.WALL);
            setTile(width - 1, i, TileType.WALL);
        }
        // Labyrinthe simple
        setTile(2, 1, TileType.WALL);
        setTile(2, 2, TileType.WALL);
        setTile(4, 2, TileType.WALL);
        setTile(5, 2, TileType.WALL);
        setTile(6, 2, TileType.WALL);
        setTile(8, 2, TileType.WALL);
        setTile(2, 4, TileType.WALL);
        setTile(3, 4, TileType.WALL);
        setTile(4, 4, TileType.WALL);
        setTile(6, 4, TileType.WALL);
        setTile(7, 4, TileType.WALL);

        // Camera et Butin
        cameraX = 4;
        cameraY = 5;
        setTile(cameraX, cameraY, TileType.CAMERA);
        setTile(8, 5, TileType.LOOT);
        setTile(1, 8, TileType.EXIT);


        setTile(1, 3, TileType.GEM);
        setTile(5, 5, TileType.GEM);
        setTile(7, 3, TileType.GEM);

        setTile(3, 1, TileType.HIDING_SPOT);
        setTile(5, 3, TileType.HIDING_SPOT);

        // Objets
        grid[3][3].itemOnTile = ItemType.SMOKE_BOMB;
        grid[8][1].itemOnTile = ItemType.FAKE_FOOTPRINTS;
    }

    public boolean isLootCollected() {
        return lootCollected;
    }

    public boolean hasWon() {
        return hasWon;
    }

    public int getScore() { return score; }

    public boolean hasLost() { return hasLost; }

    public void useFirstItem() {
        if (!playerInventory.isEmpty()) {
            ItemType item = playerInventory.get(0);
            if (item == ItemType.SMOKE_BOMB) {
                isPlayerInvisible = true;
                invisibilityTurnsLeft = 3; // Invisible pour 3 tours
            } else if (item == ItemType.EMP_CHIP) {
                isCameraDisabled = true;
                cameraDisabledTurnsLeft = 5; // Caméra HS pour 5 tours
            } else if (item == ItemType.FAKE_FOOTPRINTS) {
                fakeFootprintX = playerX;
                fakeFootprintY = playerY;
                fakeFootprintTurnsLeft = 10; // L'empreinte reste 10 tours
            }
            playerInventory.remove(0);
        }
    }

    public List<ItemType> getPlayerInventory() {
        return playerInventory;
    }

    public boolean isPlayerInvisible() {
        return isPlayerInvisible;
    }

    public boolean isCameraDisabled() {
        return isCameraDisabled;
    }

    public int getInvisibilityTurnsLeft() {
        return invisibilityTurnsLeft;
    }

    public int getCameraX() {
        return cameraX;
    }

    public int getCameraY() {
        return cameraY;
    }

    public int getFakeFootprintX() {
        return fakeFootprintX;
    }

    public int getFakeFootprintY() {
        return fakeFootprintY;
    }

    public boolean isPlayerHidden() {
        return isPlayerHidden;
    }
} 