package com.Jetvac2.MicroJam.Enemies;

import java.util.ArrayList;

import com.Jetvac2.MicroJam.Player.ChroniteManager;
import com.Jetvac2.MicroJam.Player.Player;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;

public class EnemyManager {
    private static ArrayList<BaseEnemy> enemyList = new ArrayList<BaseEnemy>();
    private static double enemySpawnInterval = 3000;
    private static double enemySpawnNext = 0;
    private static float allowedTier = 1;
    private static int maxEnemyCount = 20;

    public static void updateEnemies(float dt, float[] worldSize, SpriteBatch spriteBatch, float[] playerPosition, float[] playerSize) {
        spawnEnemies(dt, worldSize, playerPosition, playerSize);
        for(int i = 0; i < enemyList.size(); i++) {
            BaseEnemy enemy = enemyList.get(i);
            if(enemy.HP <= 0) {
                ChroniteManager.spawnChronite(enemy.droppedChronite, enemy.getEnemyPose());
                enemy.enemyHitBox.active = false;
                enemyList.remove(i);
                i-=1;
            } else {
                enemy.updateEnemy(dt, worldSize, spriteBatch, playerPosition, playerSize);
            }
        }
    }

    private static void spawnEnemies(float dt, float[] worldSize, float[] playerPosition, float[] playerSize) {
        if(System.currentTimeMillis() > enemySpawnNext) {
            if(enemyList.size() >= maxEnemyCount) {
                enemyList.remove(0);
            }
            enemySpawnNext = System.currentTimeMillis() + enemySpawnInterval;
            float spawnDistence = (worldSize[0] + worldSize[1]) / 2;
            float spawnAngle = (int)(Math.random() * 361);
            float spawnX = (float)Math.cos(Math.toRadians((double)spawnAngle)) * spawnDistence;
            float spawnY = (float)Math.sin(Math.toRadians((double)spawnAngle)) * spawnDistence;
            spawnX += playerPosition[0] + playerSize[0]/2;
            spawnY += playerPosition[1] + playerSize[1]/2;
            float[] spawnPosition = new float[] {spawnX, spawnY};

            int tier = (int)(Math.random() * (int)(allowedTier-1)) + 1;
            BaseEnemy enemy; 
            switch (tier) {
                case 1:
                    enemy = new BaseEnemy("Tier1",
                        new String[] {"Sprites/Enemies/Tier1/EnemySpriteBase.png", "Sprites/Enemies/Tier1/EnemySprite3.png"},
                        new float[][] {new float[] {.25f, .25f}, new float[] {.25f, .25f}},
                        30, 1f, 1.2f, 10f, 3, spawnPosition);
                    //allowedTier += .25f;
                    break;
              
                default:
                    enemy = null; //new BaseEnemy("Tier1", "Sprites/Enemies/Tier1EnemyTex.png", 20, 2f, 1.2f, 5f, 5, spawnPosition);
            }
            enemyList.add(enemy);
        }
    }

  
}
