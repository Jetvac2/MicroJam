package com.Jetvac2.MicroJam.Enemies;

import java.util.ArrayList;

import com.Jetvac2.MicroJam.Player.Player;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;

public class EnemyManager {
    private static ArrayList<BaseEnemy> enemyList = new ArrayList<BaseEnemy>();
    private static double enemySpawnInterval = 1500;
    private static double enemySpawnNext = 0;
    private static float allowedTier = 1;
    
    public EnemyManager() {
        
    }

    public static void updateEnemies(float dt, float[] worldSize, SpriteBatch spriteBatch, float[] playerPosition, float[] playerSize) {
        spawnEnemies(dt, worldSize, playerPosition, playerSize);
        for(int i = 0; i < enemyList.size(); i++) {
            BaseEnemy enemy = enemyList.get(i);
            enemy.updateEnemy(dt, worldSize, spriteBatch, playerPosition, playerSize);
            if(enemy.HP <= 0) {
                Player.numChronite = Math.min(Player.maxChronite, Player.numChronite + enemy.droppedChronite);
                enemy.enemyHitBox.active = false;
                enemyList.remove(i);
            }
        }
    }

    private static void spawnEnemies(float dt, float[] worldSize, float[] playerPosition, float[] playerSize) {
        if(System.currentTimeMillis() > enemySpawnNext) {
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
                    enemy = new BaseEnemy("Tier1", "Sprites/Enemies/Tier1EnemyTex.png", 15, 1f, 1.2f, 10f, 5f, spawnPosition);
                    //allowedTier += .25f;
                    break;
                case 2:
                    enemy = new BaseEnemy("Tier1", "Sprites/Enemies/Tier1EnemyTex.png", 20, 2f, 1.2f, 5f, 5f, spawnPosition);
                    //allowedTier += .3f;
                    break;
                default:
                    enemy = new BaseEnemy("Tier1", "Sprites/Enemies/Tier1EnemyTex.png", 20, 2f, 1.2f, 5f, 5f, spawnPosition);
            }
            enemyList.add(enemy);
        }
    }

  
}
