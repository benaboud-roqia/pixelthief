package com.todolist.pixelthief;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;

import java.util.HashMap;

public class SoundManager {
    public enum Sound {
        PLAYER_MOVE,
        USE_ITEM,
        VICTORY,
        GAME_OVER,
        COLLECT_GEM
    }

    private static SoundManager instance;
    private MediaPlayer musicPlayer;
    private SoundPool soundPool;
    private HashMap<Sound, Integer> soundMap;
    private Context context;
    private boolean isSoundEnabled = true;

    private SoundManager(Context context) {
        this.context = context.getApplicationContext();
        loadSounds();
    }

    public static synchronized SoundManager getInstance(Context context) {
        if (instance == null) {
            instance = new SoundManager(context);
        }
        return instance;
    }

    private void loadSounds() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(attributes)
                .build();

        soundMap = new HashMap<>();
        // IMPORTANT: Tu dois ajouter des fichiers sonores dans res/raw
        // avec les noms : player_move.ogg, use_item.ogg, victory.ogg, game_over.ogg
        soundMap.put(Sound.PLAYER_MOVE, soundPool.load(context, R.raw.player_move, 1));
        soundMap.put(Sound.USE_ITEM, soundPool.load(context, R.raw.use_item, 1));
        soundMap.put(Sound.VICTORY, soundPool.load(context, R.raw.victory, 1));
        soundMap.put(Sound.GAME_OVER, soundPool.load(context, R.raw.game_over, 1));
        soundMap.put(Sound.COLLECT_GEM, soundPool.load(context, R.raw.collect_gem, 1));
    }

    public void playSound(Sound sound) {
        if (isSoundEnabled && soundMap.containsKey(sound) && soundMap.get(sound) > 0) {
            soundPool.play(soundMap.get(sound), 1.0f, 1.0f, 1, 0, 1.0f);
        }
    }

    public void startMusic() {
        if (musicPlayer == null) {
            // IMPORTANT: Tu dois ajouter un fichier 'background_music.mp3' dans res/raw
            musicPlayer = MediaPlayer.create(context, R.raw.background_music);
            if (musicPlayer == null) return; // Fichier non trouvé
            musicPlayer.setLooping(true);
            musicPlayer.setVolume(0.5f, 0.5f);
        }
        if (!musicPlayer.isPlaying()) {
            musicPlayer.start();
        }
    }

    public void pauseMusic() {
        if (musicPlayer != null && musicPlayer.isPlaying()) {
            musicPlayer.pause();
        }
    }

    public void stopMusic() {
        if (musicPlayer != null) {
            musicPlayer.release();
            musicPlayer = null;
        }
    }

    public void release() {
        stopMusic();
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        instance = null;
    }
} 