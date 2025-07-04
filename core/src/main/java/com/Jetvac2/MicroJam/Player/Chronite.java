package com.Jetvac2.MicroJam.Player;

import com.Jetvac2.MicroJam.Util.Collider;
import com.Jetvac2.MicroJam.Util.Globals;
import com.Jetvac2.MicroJam.Util.Utils;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;

public class Chronite {
    private Sprite sprite;
    private Texture tex;
    private Collider collider;
    public boolean collected = false;
    private float maxSpawnDistencePerAxis = .2f;
    private float[][] colors = new float[3][4];
    private double colorChangeTime = 700;
    private double colorChangeEndTime = -1;
    private int nextColorIndex = (int)(Math.random() * 2);
    private int currentColorIndex = (int)(Math.random() * 2);
    private float degreesPerSecond = (int)(Math.random() * 360) - 180;
    public Chronite(float[] spawnOrgin) {
        this.tex = new Texture("Sprites/ChroniteShard.png");
        this.sprite = new Sprite(tex);
        this.sprite.setSize(.075f, .075f);
        this.sprite.setOriginCenter();
        this.collider = new Collider(new Polygon(new float[] {
            0f, 0f,
            this.sprite.getWidth(), 0f,
            this.sprite.getWidth(), this.sprite.getHeight(),
            0f, this.sprite.getHeight()
        }), "Chronite");
        float[] spawnPoint = new float[] {
            (spawnOrgin[0] - maxSpawnDistencePerAxis) + ((int)(Math.random() * maxSpawnDistencePerAxis * 100f) / 100f),
            (spawnOrgin[1] - maxSpawnDistencePerAxis) + ((int)(Math.random() * maxSpawnDistencePerAxis * 100f) / 100f)
        };
        this.sprite.setPosition(spawnPoint[0], spawnPoint[1]);
        this.collider.colliderPoly.setPosition(this.sprite.getX(), this.sprite.getY());
        colors = new float[][] {
            new float[] {.506f, .129f, 1f, .3f},
            new float[] {.506f, .129f, .8f, .9f},
            new float[] {.606f, .529f, 1f, .6f}
        };
    }

    public void updateChronite(float dt, SpriteBatch spriteBatch) {
        this.sprite.rotate(this.degreesPerSecond * dt);
        if(colorChangeEndTime < System.currentTimeMillis()) {
            this.colorChangeEndTime = System.currentTimeMillis() + this.colorChangeTime;
            if(this.nextColorIndex == 2) {
                this.nextColorIndex = 0;
            } else {
                this.nextColorIndex++;
            }
            if(this.currentColorIndex == 2) {
                this.currentColorIndex = 0;
            } else {
                this.currentColorIndex++;
            }
        }
        float[] currentColor = Utils.interpolateColor(
            colors[this.currentColorIndex], colors[nextColorIndex],
            (float)this.colorChangeTime, (float)(this.colorChangeEndTime - System.currentTimeMillis()));
        this.sprite.setColor(currentColor[0], currentColor[1], currentColor[2], currentColor[3]);
        // Update color
        this.sprite.draw(spriteBatch);
        checkCollisions();
    }

    private void checkCollisions() {
        for(Collider collider : Globals.colliders) {
            if(collider.active) {
                if(collider.name.equals("Player")) {
                    if(Intersector.overlapConvexPolygons(this.collider.colliderPoly, collider.colliderPoly)){
                        collected = true;
                    }
                } 
            }
        }
    }
}
