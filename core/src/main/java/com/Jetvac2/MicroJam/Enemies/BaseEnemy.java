package com.Jetvac2.MicroJam.Enemies;

import java.util.Vector;

import com.Jetvac2.MicroJam.Player.Player;
import com.Jetvac2.MicroJam.Util.Collider;
import com.Jetvac2.MicroJam.Util.Globals;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
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
    private Sound playerHitSound;
    public State state;
    private Vector2 defenceOffset;

    public BaseEnemy(String enemyType, String[] textureFiles, float[][] scale, float HP, float speed, float hpSpeedMult, float chroniteDamage, int droppedChronite, float[] spawnPosition, float score) {
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
        this.enemyHitBox.data = new float[] {chroniteDamage, score};
        enemyHitBox.colliderPoly.setOrigin(enemySprites[0].getWidth() / 2f, enemySprites[0].getHeight() / 2f);
        this.HP = HP;
        this.maxHP = HP;
        this.speed = speed;
        this.droppedChronite = droppedChronite;
        this.spawnPosition = spawnPosition;
        this.hpSpeedMult = hpSpeedMult;
        this.playerHitSound = Gdx.audio.newSound(Gdx.files.internal("SoundEffects/PlayerHit.wav"));
        this.defenceOffset = new Vector2(MathUtils.randomSign() * MathUtils.random(.3f, .45f), MathUtils.randomSign() * MathUtils.random(.3f, .45f));
        Globals.colliders.add(this.enemyHitBox);
    }

    public static enum State {
        ATTACKER1,
        ATTACKER2,
        ATTACKER3,
        ATTACKER4,
        DEFEND
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

        Vector2 playerPosition = new Vector2(playerPose[0] + playerSize[0]/2, playerPose[1] + playerSize[1]/2);
        Vector2 enemyPosition = new Vector2(this.getEnemyPoseVec());
        enemyPosition.x += this.getEnemySize()[0]/2;
        enemyPosition.y += this.getEnemySize()[1]/2;
        Vector2 goalPosition;
        if(state != null) {
            goalPosition = BaseEnemy.calcGoal(state, playerPosition, playerSize);
        } else {
            goalPosition = new Vector2(playerPosition).add(defenceOffset);
        }
        Vector2 positionDelta = new Vector2(enemyPosition).sub(goalPosition);
        
        Vector2 speedVector = new Vector2(positionDelta).nor().scl(-this.speed * this.hpSpeedMult * (HP / maxHP)).scl(dt);
        
        if((state != null || Math.abs(positionDelta.len()) > .125f) && !Globals.freezeTime) {
            this.enemySprites[0].translate(speedVector.x, speedVector.y);
        }

        float rotationGoal;
        float degs;
        if(Math.abs(positionDelta.len()) < .125f) {
            Vector2 delta = new Vector2(enemyPosition).sub(playerPosition);
            rotationGoal = (float)Math.toDegrees(Math.atan2(delta.y, delta.x)) + 90f;
            degs = MathUtils.lerpAngleDeg(this.enemySprites[0].getRotation(), rotationGoal, lerpConstant * dt);
        } else {
            rotationGoal = (float)Math.toDegrees(Math.atan2(positionDelta.y, positionDelta.x)) + 90f;
            degs = MathUtils.lerpAngleDeg(this.enemySprites[0].getRotation(), rotationGoal, lerpConstant * dt);
        }

        if(!Globals.freezeTime) {
            this.enemySprites[0].setRotation(degs);
        }
        

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
                    if(Intersector.overlapConvexPolygons(this.enemyHitBox.colliderPoly, collider.colliderPoly) && Globals.canHitPlayer) {
                        this.dirty = true;
                        Globals.canHitPlayer = false;
                        if(Player.numChronite - this.enemyHitBox.data[0] > 1) {
                            Player.numChronite -= this.enemyHitBox.data[0];
                        } else {
                            Player.numChronite = 1;
                        } 
                        this.playerHitSound.play(Globals.soundEffectAudioLevel);
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

    public Vector2 getEnemyPoseVec() {
        return new Vector2(this.enemySprites[0].getX(), this.enemySprites[0].getY());
    }

    public float[] getEnemySize() {
        return new float[] {this.enemySprites[0].getWidth(), this.enemySprites[0].getHeight()};
    }

    public static Vector2 calcGoal(State state, Vector2 playerPosition, float[] playerSize) {
        if(state == State.ATTACKER1) {
            return new Vector2(playerPosition).add(new Vector2(playerSize[0]/2, 0));
        } else if(state == State.ATTACKER2) {
            return new Vector2(playerPosition).sub(new Vector2(playerSize[0]/2, 0));
        } else if(state == State.ATTACKER3) {
            return new Vector2(playerPosition).add(new Vector2(0, playerSize[0]/2));
        } else if(state == State.ATTACKER4) {
            return new Vector2(playerPosition).sub(new Vector2(0, playerSize[0]/2));
        } else {
            return new Vector2();
        }
    }
}

