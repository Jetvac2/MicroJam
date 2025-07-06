package com.Jetvac2.MicroJam.Enemies;

import java.util.ArrayList;
import java.util.Vector;

import com.Jetvac2.MicroJam.Player.ChroniteManager;
import com.Jetvac2.MicroJam.Util.Globals;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class EnemyManager {
    private static boolean inStartState = true;

    private static ArrayList<BaseEnemy> enemyList = new ArrayList<BaseEnemy>();
    private static float minSpawnDis = 1.4f;
    private static float maxSpawnDis = 3f;
    private static float spawnAngleRange = 45f;
    private static float minSpawnDistenceFromOther = .35f;
    private static double wavePauseTime = 2000;//3000;
    private static double enemySpawnInterval = 1300;//1000;
    private static double enemySpawnNext = System.currentTimeMillis() + enemySpawnInterval;
    private static int maxEnemyCount = 10;
    private static int enemiesKilled = 0;
    private static int waveThreashhold = 4;

    public static void updateEnemies(float dt, float[] worldSize, SpriteBatch spriteBatch, float[] playerPosition, float[] playerSize, float[] playerVelocity) {
        
        if(Globals.gameGoing) {
            if(enemySpawnNext == -1) {
                enemySpawnNext = System.currentTimeMillis() + enemySpawnInterval/2;
            }
            spawnEnemies(dt, worldSize, playerPosition, playerSize, playerVelocity);
            for(int i = 0; i < enemyList.size(); i++) {
                BaseEnemy enemy = enemyList.get(i);
                if(enemy.HP <= 0 || enemy.dirty) {
                    if(!enemy.dirty) {
                        ChroniteManager.spawnChronite(enemy.droppedChronite, enemy.getEnemyPose());
                        Globals.score += enemy.enemyHitBox.data[1];
                    }
                    enemy.enemyHitBox.active = false;
                    enemyList.remove(i);
                    enemiesKilled++;
                    i--;
                } else {
                    enemy.updateEnemy(dt, worldSize, spriteBatch, playerPosition, playerSize);
                }
            }
        } else if(inStartState) {
            reset();
        }
        
    }

    private static void spawnEnemies(float dt, float[] worldSize, float[] playerPosition, float[] playerSize, float[] playerVelocity) {
        
        if(Globals.freezeTime) {
            enemySpawnNext += dt/1000f;
        } else if (System.currentTimeMillis() > enemySpawnNext) {
            if (enemyList.size() >= maxEnemyCount) {
                enemyList.remove(0);
            }

            if(enemiesKilled >= waveThreashhold-1) {
                enemySpawnNext = System.currentTimeMillis() +  wavePauseTime;
                enemiesKilled = 0;
            } else {
                enemySpawnNext = System.currentTimeMillis() + enemySpawnInterval;
            }
            
            
            Vector2 playerVelocityVec = new Vector2(playerVelocity[0], playerVelocity[1]);
            Vector2 spawnPosition;
            if(playerVelocityVec.len() != 0) {
                spawnPosition = playerVelocityVec.nor();
            } else {
                spawnPosition = new Vector2(MathUtils.random(-1, 1), MathUtils.random(-1, 1)).nor();
            }
            
            spawnPosition.scl(MathUtils.random(minSpawnDis, maxSpawnDis));
            spawnPosition.add(new Vector2(playerPosition[0] - playerSize[0], playerPosition[1] - playerSize[1]));
            float spawnAngle = MathUtils.random(-spawnAngleRange/2, spawnAngleRange/2);
            spawnPosition.rotateDeg(spawnAngle);

            int attempts = 0;
            for(int i = 0; i < enemyList.size() && attempts < 10; i++) {
                BaseEnemy enemy = enemyList.get(i);
                float dis = enemy.getEnemyPoseVec().dst(spawnPosition);
                if(dis < minSpawnDistenceFromOther) {
                    spawnPosition.rotateDeg(-spawnAngle);
                    spawnAngle = MathUtils.random(-spawnAngleRange/2, spawnAngleRange/2);
                    spawnPosition.rotateDeg(spawnAngle);
                    attempts++;
                }
            } 
                
            BaseEnemy enemy = new BaseEnemy("Tier1",
                new String[] {
                    "Sprites/Enemies/Tier1/EnemySpriteBase.png",
                    "Sprites/Enemies/Tier1/EnemySprite3.png"
                },
                new float[][] {
                    new float[] {.25f, .25f},
                    new float[] {.25f, .25f}
                },
                30, .8f, 1.2f, 5f, 4, new float[] {spawnPosition.x, spawnPosition.y}, Globals.scoreAddPerTier1Enemy);

                enemyList.add(enemy);            
        }
    }

    private static void reset() {
        enemiesKilled = 0;
        enemySpawnNext = -1;
        for(int i = 0; i < enemyList.size(); i++) {
            enemyList.get(i).enemyHitBox.active = false;
            enemyList.remove(i);
            i--;
        }
    }

  
}
