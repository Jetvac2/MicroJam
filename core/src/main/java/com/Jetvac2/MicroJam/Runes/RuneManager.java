package com.Jetvac2.MicroJam.Runes;

import java.util.ArrayList;
import java.util.HashSet;

import com.Jetvac2.MicroJam.Util.Globals;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class RuneManager {
    private static final ArrayList<Sprite> runes = new ArrayList<>();
    private static final Texture[] textures = new Texture[4];
    private static final float CHUNK_SIZE = 10f;
    private static final float runesPerSqrUnit = .75f;
    private static final float SPAWN_RADIUS = 2; 
    private static final float RUNE_SIZE = 0.1f;

    private static final HashSet<String> populatedChunks = new HashSet<>();
    private static float spawnTimer = 0;

    private static final float MAX_DISTANCE = 30f; 
    private static int cleanupIndex = 0;
    private static final int CLEANUP_PER_FRAME = 8;


    public static void init(float playerX, float playerY) {
        textures[0] = new Texture("Sprites/Runes/Runes1.png");
        textures[1] = new Texture("Sprites/Runes/Runes2.png");
        textures[2] = new Texture("Sprites/Runes/Runes3.png");
        textures[3] = new Texture("Sprites/Runes/Runes4.png");

        // Spawn a large batch around the player
        spawnNearbyChunks(playerX, playerY, 3f / CHUNK_SIZE); // 10f radius
    }

    public static void update(float dt, float playerX, float playerY, SpriteBatch spriteBatch) {
        spawnTimer += dt;
        if (spawnTimer >= 2f) {
            spawnTimer = 0;
            spawnNearbyChunks(playerX, playerY);
        }

        float r = 1 - Globals.backgroundColor[0];
        float g = 1 - Globals.backgroundColor[0];
        float b = 1 - Globals.backgroundColor[0];

        for (Sprite rune : runes) {
            rune.setColor(r, g, b, 1f);
            rune.draw(spriteBatch);
        }

        cleanupFarRunes(playerX, playerY);
    }

    private static void spawnNearbyChunks(float playerX, float playerY, float radiusInChunks) {
        int centerChunkX = MathUtils.floor(playerX / CHUNK_SIZE);
        int centerChunkY = MathUtils.floor(playerY / CHUNK_SIZE);

        int radius = MathUtils.ceil(radiusInChunks);

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                int chunkX = centerChunkX + dx;
                int chunkY = centerChunkY + dy;
                String key = chunkX + "," + chunkY;
                if (!populatedChunks.contains(key)) {
                    populatedChunks.add(key);
                    spawnRunesInChunk(chunkX, chunkY);
                }
            }
        }
    }

    private static void spawnNearbyChunks(float playerX, float playerY) {
        spawnNearbyChunks(playerX, playerY, SPAWN_RADIUS);
    }

    private static void spawnRunesInChunk(int chunkX, int chunkY) {
        float originX = chunkX * CHUNK_SIZE;
        float originY = chunkY * CHUNK_SIZE;
        float runeSpace = 1f / runesPerSqrUnit;
        for (float x = 0; x < CHUNK_SIZE; x += runeSpace) {
            for (float y = 0; y < CHUNK_SIZE; y += runeSpace) {
                float dx = MathUtils.random(0, .33f);
                float dy = MathUtils.random(0, .33f);
                Sprite rune = new Sprite(textures[MathUtils.random(0, 3)]);
                rune.setSize(RUNE_SIZE, RUNE_SIZE);
                rune.setPosition(originX + x + dx, originY + y + dy);
                rune.setOriginCenter();
                rune.setRotation(MathUtils.random(0f, 360f));
                rune.setAlpha(MathUtils.random(0.3f, 0.6f));
                runes.add(rune);
            }
        }
    }

    public static void reset() {
        runes.clear();
        populatedChunks.clear();
        spawnTimer = 0;
    }

    private static void cleanupFarRunes(float playerX, float playerY) {
        int checked = 0;

        while (checked < CLEANUP_PER_FRAME && !runes.isEmpty()) {
            if (cleanupIndex >= runes.size()) {
                cleanupIndex = 0;
            }

            Sprite rune = runes.get(cleanupIndex);
            float dx = rune.getX() - playerX;
            float dy = rune.getY() - playerY;
            float dist2 = dx * dx + dy * dy;

            if (dist2 > MAX_DISTANCE * MAX_DISTANCE) {
                runes.remove(cleanupIndex);
            } else {
                cleanupIndex++;
            }

            checked++;
        }
    }
}