package com.Jetvac2.MicroJam.Player;

import java.util.ArrayList;

import com.Jetvac2.MicroJam.Util.Collider;
import com.Jetvac2.MicroJam.Util.Globals;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Player {
    private Sprite playerSprite;
    private Sprite spriteLayer2;
    private Sprite spriteLayer3;
    private Sprite spriteLayer4;
    public Collider playerHitBox;
    private ArrayList<Bullet> bulletList = new ArrayList<Bullet>();

    private float[] velocity = new float[] {0f, 0f};  
    private float acceleration = 5f;                         
    private float deceleration = 20f;
    private float maxSpeed = 1f;

    private boolean setPlayerStartPosition = true;

    public static float maxChronite = 21;
    public static float numChronite = 21;

    private double IFrameTime = 670;
    private double IFrameEndTime = -1;

    private double fireCooldown = 700;
    private double fireCooldownEndTime = -1;
    private float bulletSpawnOffset = 0f;
    private float bulletCost = 1f;
    private float chroniteLossPerSecond = .5f;

    private float[] spriteLayer4BaseSize;

    private Sound bulletFireSound;
    private Sound playerHitSound;
    private float trueMaxSpeed = 2f;
    private float alpha = 1f;

    public Player() {
        this.playerSprite = new Sprite(new Texture("Sprites/Player/PlayerTexBase.png"));
        this.playerSprite.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        this.spriteLayer2 = new Sprite(new Texture("Sprites/Player/PlayerTex2.png"));
        this.spriteLayer2.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        this.spriteLayer3 = new Sprite(new Texture("Sprites/Player/PlayerTex3.png"));
        this.spriteLayer3.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        this.spriteLayer4 = new Sprite(new Texture("Sprites/Player/PlayerTex4.png"));
        this.spriteLayer4.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        this.playerSprite.setSize(.25f, .25f);
        this.spriteLayer2.setSize(.25f, .25f);
        this.spriteLayer3.setSize(.23f, .23f);
        this.spriteLayer4BaseSize = new float[] {.15f, .15f};
        this.spriteLayer4.setSize(this.spriteLayer4BaseSize[0], this.spriteLayer4BaseSize[1]);
        this.spriteLayer2.setOriginCenter();
        this.spriteLayer3.setOriginCenter();
        this.spriteLayer4.setOriginCenter();
        this.playerHitBox = new Collider(new Polygon(new float[]{
            0f, 0f,
            this.playerSprite.getWidth(), 0f,
            this.playerSprite.getWidth(), this.playerSprite.getHeight(),
            0f, this.playerSprite.getHeight()
        }), "Player");
        Globals.colliders.add(this.playerHitBox);

        this.bulletFireSound = Gdx.audio.newSound(Gdx.files.internal("SoundEffects/bulletFire.wav"));
        this.playerHitSound = Gdx.audio.newSound(Gdx.files.internal("SoundEffects/PlayerHit.wav"));
    }

    public void updatePlayer(float dt, Viewport worldViewport, float[] worldSize, SpriteBatch spriteBatch) {
        if(setPlayerStartPosition) {
            this.playerSprite.setPosition(worldSize[0]/2, worldSize[1]/2);
            this.setPlayerStartPosition = false;
        }
        if(numChronite - chroniteLossPerSecond * dt > 1) {
            numChronite -= chroniteLossPerSecond * dt;
        } else {
            Globals.gameGoing = false;
        }

        if(!Globals.gameGoing) {
            Gdx.graphics.setWindowedMode(20, 20);
        }
        
        input(dt, worldViewport);
        logic(dt, worldViewport);
        render(dt, worldSize, spriteBatch, worldViewport);
    }

    private void input(float dt, Viewport worldViewport) {
        // Base Player Movement
        // Horizontal movement
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            velocity[0] += acceleration * dt;
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
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
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            velocity[1] += acceleration * dt;
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
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

        

        // Slow Time and AOE

        if(Gdx.input.isButtonPressed(Input.Buttons.LEFT) ||
            Gdx.input.isButtonPressed(Input.Buttons.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.R)) {
            spawnBullet(dt, worldViewport);
        }

        Vector2 velocityVec = new Vector2(velocity[0], velocity[1]);

        if (Math.abs(velocityVec.len()) > maxSpeed) {
            velocityVec.nor().scl(maxSpeed);
        }

        float chroniteRatio = maxChronite / numChronite;
        Vector2 scaledVec = new Vector2(velocityVec).scl(chroniteRatio);
        if (Math.abs(scaledVec.len()) > trueMaxSpeed) {
            scaledVec.nor().scl(trueMaxSpeed);
        }

        playerSprite.translate(scaledVec.x * dt, scaledVec.y * dt);

    }

    private void logic(float dt, Viewport worldViewport) {
        float playerX = playerSprite.getX() + playerSprite.getWidth() / 2f;
        float playerY = playerSprite.getY() + playerSprite.getHeight() / 2f;

        // Get current movement direction
        Vector2 movementDir = new Vector2(velocity[0], velocity[1]);
        Vector2 offset = new Vector2(movementDir).nor().scl((float)-Math.max(Math.sqrt((double)movementDir.len2()/5f), .1)).scl(.05f);
        if(Math.abs(offset.len()) > .15f) {
            offset.nor().scl(.15f);
        }
        float pixelsPerUnit = Gdx.graphics.getWidth() / worldViewport.getWorldWidth();
        Vector3 cameraTarget = new Vector3(playerX + offset.x, playerY + offset.y, 0);
        cameraTarget.x = Math.round(cameraTarget.x * pixelsPerUnit)/ pixelsPerUnit;
        cameraTarget.y = Math.round(cameraTarget.y * pixelsPerUnit)/ pixelsPerUnit;
        worldViewport.getCamera().position.set(cameraTarget);
        worldViewport.getCamera().update();
        
        this.spriteLayer2.setPosition(playerSprite.getX(), playerSprite.getY());
        this.spriteLayer3.setPosition(playerSprite.getX() + playerSprite.getWidth()/2 - this.spriteLayer3.getWidth()/2, playerSprite.getY() + playerSprite.getHeight()/2 - this.spriteLayer3.getHeight()/2);
        this.spriteLayer4.setPosition(playerSprite.getX() + playerSprite.getWidth()/2 - this.spriteLayer4.getWidth()/2, playerSprite.getY() + playerSprite.getHeight()/2 - this.spriteLayer4.getHeight()/2);
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
            double timeLeft = IFrameEndTime - System.currentTimeMillis();
            float percentLeft = (float)timeLeft / (float)this.IFrameTime;
            this.alpha = (float)Math.cos(Math.abs(percentLeft * 4 * Math.PI));

            if(this.IFrameEndTime < System.currentTimeMillis()) {
                this.IFrameEndTime = -1;
                Globals.canHitPlayer = true;
                this.alpha = 1;
            }
    
        }

        checkCollisions();
    }

    private void checkCollisions() {
        for(Collider collider : Globals.colliders) {
            if(collider.active) {
                if(collider.name.contains("Tier")) {
                    if(Intersector.overlapConvexPolygons(this.playerHitBox.colliderPoly, collider.colliderPoly) && Globals.canHitPlayer){
                        Globals.canHitPlayer = false;
                        if(numChronite - collider.data[0] > 1) {
                            numChronite -= collider.data[0];
                        } else {
                            numChronite = 1;
                        }
                        this.playerHitSound.play(Globals.soundEffectAudioLevel);
                    }
                } 
            }
        }
    }

    private void render(float dt, float[] worldSize, SpriteBatch spriteBatch, Viewport worldViewport) {
        float chroniteRatioSmall = (numChronite / maxChronite);
        float chroniteRatioLarge = (maxChronite / numChronite);
        this.spriteLayer2.rotate(90 * chroniteRatioSmall * dt);
        this.spriteLayer3.rotate(-120 * chroniteRatioSmall * dt );
        this.spriteLayer4.setSize(this.spriteLayer4BaseSize[0] * (numChronite / maxChronite), this.spriteLayer4BaseSize[1] * (numChronite / maxChronite));
        this.playerSprite.setColor(.2f * chroniteRatioLarge - .4f, .4f * chroniteRatioSmall + .2f, .4f * chroniteRatioSmall + .2f , this.alpha);
        this.spriteLayer2.setColor(.2f * chroniteRatioLarge - .2f, .5f * chroniteRatioSmall + .3f, .6f * chroniteRatioSmall + .4f , this.alpha);
        this.spriteLayer3.setColor(.2f * chroniteRatioLarge - .1f, .6f * chroniteRatioSmall + .4f, .6f * chroniteRatioSmall + .4f , this.alpha);
        this.spriteLayer4.setColor(1, 1, 1, 1f * chroniteRatioSmall + .25f);
        
        spriteBatch.setProjectionMatrix(worldViewport.getCamera().view);
        spriteBatch.begin();
        this.playerSprite.draw(spriteBatch);
        this.spriteLayer2.draw(spriteBatch);
        this.spriteLayer3.draw(spriteBatch);
        this.spriteLayer4.draw(spriteBatch);
        for(int i = 0; i < bulletList.size(); i++) {
            Bullet bullet = this.bulletList.get(i);
            if(bullet.isDead) {
                bullet.collider.active = false;
                this.bulletList.remove(i);
                i-=1;
            } else {
                this.bulletList.get(i).updateBullet(dt, spriteBatch);
            }
        }
        spriteBatch.end();
        
    }

    public float[] getPlayerPose() {
        return new float[] {this.playerSprite.getX(), this.playerSprite.getY()};
    }

    public float[] getPlayerSize() {
        return new float[] {this.playerSprite.getWidth(), this.playerSprite.getHeight()};
    }

    private void spawnBullet(float dt, Viewport wordViewport) {
        if (this.fireCooldownEndTime < System.currentTimeMillis()) {
            this.bulletFireSound.play(Globals.musicAudioLevel);
            this.fireCooldownEndTime = System.currentTimeMillis() + this.fireCooldown;
            if(numChronite - bulletCost>= 1) {
                numChronite -= bulletCost;
            } else {
                Globals.gameGoing = false;
            } 
            Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            mousePos = wordViewport.unproject(mousePos);

            float playerX = playerSprite.getX() + playerSprite.getWidth() / 2f;
            float playerY = playerSprite.getY() + playerSprite.getHeight() / 2f;

            float dx = mousePos.x - playerX;
            float dy = mousePos.y - playerY;

            Vector2 direction = new Vector2(dx, dy).nor();

            float[] spawnPosition = new float[] {
                playerX + direction.x * bulletSpawnOffset,
                playerY + direction.y * bulletSpawnOffset
            };

            float[] velocity = new float[] {direction.x, direction.y };

            float angleDeg = MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees - 90f;

            this.bulletList.add(new Bullet(spawnPosition, velocity, angleDeg));
        }
    }

}
