package com.Jetvac2.MicroJam.Player;

import java.util.Vector;

import com.Jetvac2.MicroJam.Util.Collider;
import com.Jetvac2.MicroJam.Util.Globals;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public class Bullet {
    // More chronite = less damge longer life
    private Texture texture;
    private Sprite sprite;
    public Collider collider;
    private float[] velocity;
    private double startTime;
    private double maxLifeTime = 1500;
    private double lifeTime;
    private float baseDamage = 5f;
    private float powerMult;
    public boolean isDead = false;
    private float maxVelocity = 5f;
    public Bullet(float[] spawnPose, float[] velocity, float angleDeg) {
        this.texture = new Texture("Sprites/Bullet.png");
        this.sprite = new Sprite(texture);
        this.sprite.setSize(.1f, .1f);
        this.sprite.setPosition(spawnPose[0] - this.sprite.getWidth() / 2f,
            spawnPose[1] - this.sprite.getHeight() / 2f
        );
        this.sprite.setOriginCenter();
        this.sprite.setRotation(angleDeg);

        this.collider = new Collider(new Polygon(new float[] {
            0f, 0f,
            this.sprite.getWidth(), 0f,
            this.sprite.getWidth()/2, this.sprite.getHeight()
        }), "Bullet");
        this.collider.colliderPoly.setOrigin(this.sprite.getWidth()/2, this.sprite.getHeight()/2);
        this.velocity = velocity;
        this.startTime = System.currentTimeMillis();
        this.lifeTime = maxLifeTime * Player.numChronite / Player.maxChronite;
        this.powerMult = (Player.maxChronite / Player.numChronite) * 1.5f;
        Globals.colliders.add(this.collider);
    }

    public void updateBullet(float dt, SpriteBatch spriteBatch) {
        float power = calcPower();
        this.collider.data = new float[] {power * baseDamage};
        float[] modVelocity = new float[] {velocity[0] * power, velocity[1] * power};
        Vector2 velocity = new Vector2(modVelocity[0], modVelocity[1]);
        float mag = Math.abs(velocity.len());
        float velocityScl = 1;
        if(mag > maxVelocity) {
            velocityScl = maxVelocity / mag;
        }
        velocity.scl(velocityScl);
        velocity.scl(dt);
        this.sprite.translate(velocity.x, velocity.y);

        this.collider.colliderPoly.setVertices(new float[] {
            0f, 0f,
            this.sprite.getWidth(), 0f,
            this.sprite.getWidth()/2, this.sprite.getHeight()
        });
        this.collider.colliderPoly.setPosition(this.sprite.getX(), this.sprite.getY());
        this.collider.colliderPoly.setRotation(this.sprite.getRotation());
        
        sprite.draw(spriteBatch);
        checkCollisions();
    }

    private void checkCollisions() {
        for(Collider collider : Globals.colliders) {
            if(collider.active) {
                if(Intersector.overlapConvexPolygons(this.collider.colliderPoly, collider.colliderPoly)) {
                    if(!collider.name.equals("Player") && !collider.name.equals("Bullet")) {
                        this.isDead = true;

                        Globals.bulletExplodingSoundEffect.setVolume(Globals.soundEffectAudioLevel);
                        Globals.bulletExplodingSoundEffect.setLooping(false);
                        Globals.bulletExplodingSoundEffect.play();
                    }
                }
            }
        }
    }

    private float calcPower() {
        double timePassed = System.currentTimeMillis() - this.startTime;
        this.sprite.setColor(1, (float)lifeTime / (float)timePassed - 1, (float)lifeTime / (float)timePassed - 1, (float)lifeTime / (float)timePassed - 1);
        if(timePassed > lifeTime) {
            this.isDead = true;
            return 0;
        }
        return (float)(powerMult * (lifeTime / (timePassed + lifeTime)));
    }
}
