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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class BaseEnemy {
    private Sprite[] enemySprites;
    public Collider enemyHitBox;
    private boolean setStartPosition = true;
    private float lerpConstant = 5f;
    public float HP;
    private float maxHP;
    private float speed;
    private float hpSpeedMult;
    public int droppedChronite;
    private float[] spawnPosition;
    private float minSpeedMult = .5f;
    public boolean dirty = false;

    // TODO: Add argumetns for partical effects for taking damage and for dieing. 
    public BaseEnemy(String enemyType, String[] textureFiles, float[][] scale, float HP, float speed, float hpSpeedMult, float chroniteDamage, int droppedChronite, float[] spawnPosition) {
        this.enemySprites = new Sprite[textureFiles.length];
        for(int i = 0; i < enemySprites.length; i++) {
            this.enemySprites[i] = new Sprite(new Texture(textureFiles[i]));
            this.enemySprites[i].setSize(scale[i][0], scale[i][1]);
            this.enemySprites[i].setOriginCenter();
        }
         
        this.enemyHitBox = new Collider(new Polygon(new float[] {
            0f, 0f,
            this.enemySprites[0].getWidth(), 0f,
            enemySprites[0].getWidth()/2, enemySprites[0].getHeight()
        }), enemyType);
        this.enemyHitBox.data = new float[] {chroniteDamage};
        enemyHitBox.colliderPoly.setOrigin(enemySprites[0].getWidth() / 2f, enemySprites[0].getHeight() / 2f);
        this.HP = HP;
        this.maxHP = HP;
        this.speed = speed;
        this.droppedChronite = droppedChronite;
        this.spawnPosition = spawnPosition;
        this.hpSpeedMult = hpSpeedMult;
        
        Globals.colliders.add(this.enemyHitBox);
    }

    public void updateEnemy(float dt, float[] worldSize, SpriteBatch spriteBatch, float[] playerPose, float[] playerSize) {
        if(setStartPosition) {
            this.enemySprites[0].setPosition(spawnPosition[0] + this.enemySprites[0].getWidth(),
            spawnPosition[1] + this.enemySprites[0].getHeight());
            setStartPosition = false;
        }
        logic(dt,  playerPose, playerSize);
        render(dt, worldSize, spriteBatch);
    }

    private void logic(float dt, float[] playerPose, float[] playerSize) {
        float[] enemyPose = getEnemyPose();

        float dx = playerPose[0] - playerSize[0]/2 - enemyPose[0] + enemySprites[0].getWidth()/2;
        float dy = playerPose[1] - playerSize[1]/2 - enemyPose[1] + enemySprites[0].getHeight()/2;

        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        float attackRadius = 0.3f; // How close enemies must be to attack
        boolean isAttacking = distance < attackRadius;

        Vector2 separation = new Vector2();

        if (!isAttacking) {
            float separationRadius = .5f;
            for (Collider other : Globals.colliders) {
                if (other == this.enemyHitBox || !other.name.equals(this.enemyHitBox.name)) continue;

                float ox = other.colliderPoly.getX();
                float oy = other.colliderPoly.getY();
                float ex = enemyPose[0];
                float ey = enemyPose[1];

                float distSq = (ox - ex) * (ox - ex) + (oy - ey) * (oy - ey);
                if (distSq < separationRadius * separationRadius && distSq > 0.0001f) {
                    float dist = (float) Math.sqrt(distSq);
                    float push = (separationRadius - dist) / separationRadius;
                    separation.add((ex - ox) / dist * push, (ey - oy) / dist * push);
                }
            }
        }

        Vector2 toPlayer = new Vector2(dx, dy).nor().scl(speed);
        Vector2 finalVelocity = toPlayer.add(separation).nor().scl(speed);
        finalVelocity.x *=  Math.max((HP / maxHP * hpSpeedMult), this.minSpeedMult);
        finalVelocity.y *=  Math.max((HP / maxHP * hpSpeedMult), this.minSpeedMult);
  
        enemySprites[0].translate(finalVelocity.x * dt, finalVelocity.y * dt);

        float targetRotation = (float)Math.toDegrees(MathUtils.atan2(dy, dx)) - 90;
        float lerpFactor = lerpConstant * dt;
        float lerpedAngle = MathUtils.lerpAngleDeg(enemySprites[0].getRotation(), targetRotation, lerpFactor);
        enemySprites[0].setRotation(lerpedAngle);
        this.enemyHitBox.colliderPoly.setPosition(enemyPose[0], enemyPose[1]);
        this.enemyHitBox.colliderPoly.setVertices(new float[] {
            0f, 0f,
            this.enemySprites[0].getWidth(), 0f,
            enemySprites[0].getWidth()/2, enemySprites[0].getHeight()
        });
        this.enemyHitBox.colliderPoly.setRotation(this.enemySprites[0].getRotation());
        checkCollisions();
    }

    private void checkCollisions() {
        for(Collider collider : Globals.colliders) {
            if(collider.active) {
                if(collider.name.equals("Player")) {
                    if(Intersector.overlapConvexPolygons(this.enemyHitBox.colliderPoly, collider.colliderPoly) && Globals.canHitPlayer){
                        this.dirty = true;
                    }
                } else if(collider.name.equals("Bullet")) {
                    if(Intersector.overlapConvexPolygons(this.enemyHitBox.colliderPoly, collider.colliderPoly)){
                        this.HP -= collider.data[0];
                    }
                }
            }
            
        }
    }

    private void render(float dt, float[] worldSize, SpriteBatch spriteBatch) {
        for(int i = 0; i < this.enemySprites.length; i++) {
            if(i != 0) {
                this.enemySprites[i].setPosition(enemySprites[0].getX() + enemySprites[0].getWidth()/2 - this.enemySprites[i].getWidth()/2,
                enemySprites[0].getY() + enemySprites[0].getHeight()/2 - this.enemySprites[i].getHeight()/2);
                this.enemySprites[i].setRotation(enemySprites[0].getRotation());
            }
            float hpSmallRatio = this.HP / this.maxHP;
            this.enemySprites[i].setColor(1f *  hpSmallRatio, .443f , .663f * hpSmallRatio , 1f);
            this.enemySprites[i].draw(spriteBatch);
        }
    }

    public float[] getEnemyPose() {
        return new float[] {this.enemySprites[0].getX(), this.enemySprites[0].getY()};
    }

    public float[] getEnemySize() {
        return new float[] {this.enemySprites[0].getWidth(), this.enemySprites[0].getHeight()};
    }
}

