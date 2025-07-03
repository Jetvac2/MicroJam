package com.Jetvac2.MicroJam.Player;

import java.util.ArrayList;

import com.Jetvac2.MicroJam.Util.Collider;
import com.Jetvac2.MicroJam.Util.Globals;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Player {
    private Texture playerTex;
    private Sprite playerSprite;
    public Collider playerHitBox;
    private float[] velocity = new float[] {0f, 0f};          
    private float acceleration = 5f;                         
    private float deceleration = 20f;
    private float maxSpeed = 5f;

    private boolean setPlayerStartPosition = true;

    public static float numChronite;

    private double IFrameTime = 250f;
    private double IFrameEndTime = -1;

    public Player() {
        this.playerTex = new Texture("Sprites/PlayerTex.png");
        this.playerSprite = new Sprite(this.playerTex);
        this.playerSprite.setSize(.25f, .25f);
        this.playerHitBox = new Collider(new Polygon(new float[]{
            0f, 0f,
            this.playerSprite.getWidth(), 0f,
            this.playerSprite.getWidth(), this.playerSprite.getHeight(),
            0f, this.playerSprite.getHeight()
        }), "Player");
        Globals.colliders.add(this.playerHitBox);
    }

    public void updatePlayer(float dt, Viewport worldViewport, float[] worldSize, SpriteBatch spriteBatch) {
        System.out.println(Globals.canHitPlayer);
        if(setPlayerStartPosition) {
            this.playerSprite.setPosition(worldSize[0]/2, worldSize[1]/2);
            this.setPlayerStartPosition = false;
        }
        input(dt);
        logic(dt, worldViewport);
        render(dt, worldSize, spriteBatch);
    }

    private void input(float dt) {
        // Base Player Movement
        // Horizontal movement
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            velocity[0] += acceleration * dt;
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            velocity[0] -= acceleration * dt;
        } else {
            if (velocity[0] > 0) {
                velocity[0] -= deceleration * dt;
                if (velocity[0] < 0) velocity[0] = 0;
            } else if (velocity[0] < 0) {
                velocity[0] += deceleration * dt;
                if (velocity[0] > 0) velocity[0] = 0;
            }
        }

        // Vertical movement
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            velocity[1] += acceleration * dt;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            velocity[1] -= acceleration * dt;
        } else {
            if (velocity[1] > 0) {
                velocity[1] -= deceleration * dt;
                if (velocity[1] < 0) velocity[1] = 0;
            } else if (velocity[1] < 0) {
                velocity[1] += deceleration * dt;
                if (velocity[1] > 0) velocity[1] = 0;
            }
        }

        float speed = (float) Math.sqrt(velocity[0] * velocity[0] + velocity[1] * velocity[1]);
        if (speed > maxSpeed) {
            float scale = maxSpeed / speed;
            velocity[0] *= scale;
            velocity[1] *= scale;
        }

        // Dash?

        // Slow Time and AOE

        // Basic Ranged Attack

        playerSprite.translate(velocity[0] * dt, velocity[1] * dt);
    }

    private void logic(float dt, Viewport worldViewport) {
        
        float[] playerCenterPosition = new float[] {playerSprite.getX() + playerSprite.getWidth()/2, playerSprite.getY() + playerSprite.getHeight()/2};
        worldViewport.getCamera().position.lerp(new Vector3(playerCenterPosition[0], playerCenterPosition[1], 0), 0.5f);
        
        this.playerHitBox.colliderPoly.setPosition(playerSprite.getX(), playerSprite.getY());
        this.playerHitBox.colliderPoly.setVertices(new float[]{
            0f, 0f,
            this.playerSprite.getWidth(), 0f,
            this.playerSprite.getWidth(), this.playerSprite.getHeight(),
            0f, this.playerSprite.getHeight()
        });

        if(!Globals.canHitPlayer) {
            if(this.IFrameEndTime == -1) {
                this.IFrameEndTime = System.currentTimeMillis() + this.IFrameTime;
            }
            if(this.IFrameEndTime < System.currentTimeMillis()) {
                this.IFrameEndTime = -1;
                Globals.canHitPlayer = true;
            }
    
        }

        checkCollisions();
    }

    private void checkCollisions() {
        for(Collider collider : Globals.colliders) {
            if(collider.name.equals("Tier1")) {
                if(Intersector.overlapConvexPolygons(this.playerHitBox.colliderPoly, collider.colliderPoly) && Globals.canHitPlayer){
                   System.out.println("Player hit");
                   Globals.canHitPlayer = false;
                }
            } 
        }
    }

    private void render(float dt, float[] worldSize, SpriteBatch spriteBatch) {
        this.playerSprite.setColor(0f, .443f, .663f, 1f);
        this.playerSprite.draw(spriteBatch);
    }

    public float[] getPlayerPose() {
        return new float[] {this.playerSprite.getX(), this.playerSprite.getY()};
    }

    public float[] getPlayerSize() {
        return new float[] {this.playerSprite.getWidth(), this.playerSprite.getHeight()};
    }
}
