package com.Jetvac2.MicroJam.Enemies;

import java.util.ArrayList;

import com.Jetvac2.MicroJam.Player.ChroniteManager;
import com.Jetvac2.MicroJam.Player.Player;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

public class EnemyManager {
    private static ArrayList<BaseEnemy> enemyList = new ArrayList<BaseEnemy>();
    private static double wavePauseTime = 3000;
    private static double enemySpawnInterval = 1000;
    private static double enemySpawnNext = System.currentTimeMillis() + enemySpawnInterval;
    private static int maxEnemyCount = 10;
    private static int enemiesKilled = 0;
    private static int waveThreashhold = 4;
    private static int waveNumber = 1;

    public static void updateEnemies(float dt, float[] worldSize, SpriteBatch spriteBatch, float[] playerPosition, float[] playerSize) {
        spawnEnemies(dt, worldSize, playerPosition, playerSize);
        for(int i = 0; i < enemyList.size(); i++) {
            BaseEnemy enemy = enemyList.get(i);
            if(enemy.HP <= 0 || enemy.dirty) {
                if(!enemy.dirty) {
                    ChroniteManager.spawnChronite(enemy.droppedChronite, enemy.getEnemyPose());
                }
                enemy.enemyHitBox.active = false;
                enemyList.remove(i);
                enemiesKilled++;
                i-=1;
            } else {
                enemy.updateEnemy(dt, worldSize, spriteBatch, playerPosition, playerSize);
            }
        }
    }

    private static void spawnEnemies(float dt, float[] worldSize, float[] playerPosition, float[] playerSize) {
    if (System.currentTimeMillis() > enemySpawnNext) {
        if (enemyList.size() >= maxEnemyCount) {
            enemyList.remove(0);
        }

        if(enemiesKilled >= waveThreashhold-1) {
            enemySpawnNext = System.currentTimeMillis() +  wavePauseTime;
            enemiesKilled = 0;
        } else {
            enemySpawnNext = System.currentTimeMillis() + enemySpawnInterval;
        }
        int numSectors = 8;
        int[] sectorCounts = new int[numSectors];

        float playerCenterX = playerPosition[0] + playerSize[0] / 2f;
        float playerCenterY = playerPosition[1] + playerSize[1] / 2f;

        // Count enemies in each sector
        for (BaseEnemy enemy : enemyList) {
            float[] pos = enemy.getEnemyPose();
            float angle = MathUtils.atan2(pos[1] - playerCenterY, pos[0] - playerCenterX) * MathUtils.radiansToDegrees;
            if (angle < 0) angle += 360;
            int sector = (int)(angle / (360f / numSectors));
            sectorCounts[sector]++;
        }

        // Find the least populated sector
        int bestSector = 0;
        int minCount = sectorCounts[0];
        for (int i = 1; i < numSectors; i++) {
            if (sectorCounts[i] < minCount) {
                minCount = sectorCounts[i];
                bestSector = i;
            }
        }

        // Pick a random angle within that sector
        float sectorAngleSize = 360f / numSectors;
        float spawnAngle = bestSector * sectorAngleSize + MathUtils.random(sectorAngleSize);

        // Spawn at a distance from the player
        float spawnDistance = (worldSize[0] + worldSize[1]) / 2f;
        float spawnX = MathUtils.cosDeg(spawnAngle) * spawnDistance + playerCenterX;
        float spawnY = MathUtils.sinDeg(spawnAngle) * spawnDistance + playerCenterY;
        float[] spawnPosition = new float[] {spawnX, spawnY};

        // Create the enemy
        int tier = MathUtils.random(1, Math.min(waveNumber, 5));
        BaseEnemy enemy;
        switch (tier) {
            case 1:
                enemy = new BaseEnemy("Tier1",
                    new String[] {
                        "Sprites/Enemies/Tier1/EnemySpriteBase.png",
                        "Sprites/Enemies/Tier1/EnemySprite3.png"
                    },
                    new float[][] {
                        new float[] {.25f, .25f},
                        new float[] {.25f, .25f}
                    },
                    30, 1f, 1.2f, 5f, 3, spawnPosition);
                    //allowedTier += .25f;
                break;
            default:
                enemy = null;
        }
        enemyList.add(enemy);
        
    }
}


  
}
