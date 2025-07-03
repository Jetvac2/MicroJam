package com.Jetvac2.MicroJam.Enemies;

import java.util.ArrayList;

import com.Jetvac2.MicroJam.Util.Collider;
import com.Jetvac2.MicroJam.Util.Globals;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;

public class BaseEnemy {
    private Texture enemyTex;
    private Sprite enemySprite;
    public Collider enemyHitBox;
    private boolean setStartPosition = true;
    private float HP;
    private float maxHP;
    private float speed;
    private float hpSpeedMult;
    private float droppedChronite;
    private float[] spawnPosition;

    // TODO: Add argumetns for partical effects for taking damage and for dieing. 
    public BaseEnemy(String enemyType, String textureFile, float HP, float speed, float hpSpeedMult ,float droppedChronite, float[] spawnPosition) {
        this.enemyTex = new Texture(textureFile);
        this.enemySprite = new Sprite(this.enemyTex);
        this.enemySprite.setSize(.25f, .25f);
        this.enemyHitBox = new Collider(new Polygon(new float[] {
            0f, 0f,
            this.enemySprite.getWidth(), 0f,
            enemySprite.getWidth()/2, enemySprite.getHeight()
        }), enemyType);
        this.HP = HP;
        this.maxHP = HP;
        this.speed = speed;
        this.droppedChronite = droppedChronite;
        this.spawnPosition = spawnPosition;
        this.hpSpeedMult = hpSpeedMult;
        
        Globals.colliders.add(this.enemyHitBox);
    }

    public void updateEnemy(float dt, float[] worldSize, SpriteBatch spriteBatch, float[] playerPose) {
        if(setStartPosition) {
            this.enemySprite.setPosition(spawnPosition[0] + this.enemySprite.getWidth(),
            spawnPosition[1] + this.enemySprite.getHeight());
            setStartPosition = false;
        }
        logic(dt,  playerPose);
        render(dt, worldSize, spriteBatch);
    }

    private void logic(float dt, float[] playerPose) {
        float[] enemyPose = getEnemyPose();

        float dx = playerPose[0] - enemyPose[0];
        float dy = playerPose[1] - enemyPose[1];

        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        float xSpeed = 0f;
        float ySpeed = 0f;

        if (distance != 0f) {
            float scale = speed / distance;

            xSpeed = dx * scale;
            ySpeed = dy * scale;
        }
        this.enemySprite.translate(xSpeed * ((HP/maxHP) * hpSpeedMult * dt), ySpeed * ((HP/maxHP) * hpSpeedMult) * dt);

        this.enemyHitBox.colliderPoly.setPosition(enemyPose[0], enemyPose[1]);
        this.enemyHitBox.colliderPoly.setVertices(new float[] {
            0f, 0f,
            this.enemySprite.getWidth(), 0f,
            enemySprite.getWidth()/2, enemySprite.getHeight()
        });
        checkCollisions();
    }

    private void checkCollisions() {
        for(Collider collider : Globals.colliders) {
            if(collider.name.equals("Player")) {
                if(Intersector.overlapConvexPolygons(this.enemyHitBox.colliderPoly, collider.colliderPoly) && Globals.canHitPlayer){
                   System.out.println("Hit Player");
                }
            }
        }
    }

    private void render(float dt, float[] worldSize, SpriteBatch spriteBatch) {
        this.enemySprite.setColor(1f, .443f, .663f, 1f);
        this.enemySprite.draw(spriteBatch);
    }

    public float[] getEnemyPose() {
        return new float[] {this.enemySprite.getX(), this.enemySprite.getY()};
    }

    public float[] getEnemySize() {
        return new float[] {this.enemySprite.getWidth(), this.enemySprite.getHeight()};
    }
}

