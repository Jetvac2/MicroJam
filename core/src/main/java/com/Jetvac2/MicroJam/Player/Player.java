package com.Jetvac2.MicroJam.Player;

import java.util.ArrayList;

import com.Jetvac2.MicroJam.Runes.RuneManager;
import com.Jetvac2.MicroJam.Util.Collider;
import com.Jetvac2.MicroJam.Util.Globals;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Player {
    private boolean inStartState = true;

    private Sprite playerSprite;
    private Sprite spriteLayer2;
    private Sprite spriteLayer3;
    private Sprite spriteLayer4;
    public Collider playerHitBox;
    private ArrayList<Bullet> bulletList = new ArrayList<Bullet>();

    private Vector3 velocity = new Vector3();
    private float maxSpeed = 2f;
    private float trueMaxSpeed = 3f;
    private float acceleration = 9f;
    private float deceleration = 40f;


    private boolean setPlayerStartPosition = true;

    public static float maxChronite = 21;
    public static float numChronite = 21;

    private double IFrameTime = 670;
    private double IFrameEndTime = -1;

    private double fireCooldown = 500;
    private double fireCooldownEndTime = -1;
    private float bulletSpawnOffset = 0f;
    private float bulletCost = .75f;
    private float chroniteLossPerSecond = 1f;
    private float[] spriteLayer4BaseSize;


    private float freezeTimeTrigger = 2f;
    private double freezeTimeCooldownTime = 15000;
    private double freezeTimeCooldownEndTime = -1;
    private double freezeTimeMaxTime = 7500;
    private double freezeTimeEndTime = -1;
    private boolean isFreezeTimeCooldownActive = false;
    private float minChroniteToFreezeTime = 14.6f;

    private Sound bulletFireSound;
    private float alpha = 1f;

    private Sound timeFreezeEffect;
    private Sound playerDeath;


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
        this.timeFreezeEffect = Gdx.audio.newSound(Gdx.files.internal("Music/TimeFreezeEffect.mp3"));
        this.playerDeath = Gdx.audio.newSound(Gdx.files.internal("SoundEffects/PlayerDeathSound.wav"));
    }

    public void updatePlayer(float dt, Viewport worldViewport, float[] worldSize, SpriteBatch spriteBatch) {
        if(Gdx.input.justTouched() && !Globals.bulletFireEffectPrepped) {
            this.bulletFireSound.play(0f);
            Globals.bulletFireEffectPrepped = true;
        }
        if(Globals.gameGoing) {
            this.inStartState = false;
            Globals.score += Globals.scoreAddPerSecond * dt;
            if(numChronite <= 1) {
                Globals.gameGoing = false;
                Globals.scores.add(Globals.score);
                Globals.score = 0;
                this.timeFreezeEffect.stop();
                this.playerDeath.play(Globals.soundEffectAudioLevel);
                RuneManager.reset();
            }

            if(setPlayerStartPosition) {
                this.playerSprite.setPosition(worldSize[0]/2, worldSize[1]/2);
                this.setPlayerStartPosition = false;
            }
            if(numChronite - chroniteLossPerSecond * dt > 1) {
                if(!Globals.freezeTime) {
                    numChronite -= chroniteLossPerSecond * dt;
                }
            } else {
                numChronite = .8f;
            }
            
            input(dt, worldViewport);
            logic(dt, worldViewport);
            render(dt, worldSize, spriteBatch, worldViewport);
        } else if(!this.inStartState) {
            reset();
        }
    }

    private void input(float dt, Viewport worldViewport) {
        if(Gdx.input.isKeyPressed(Input.Keys.W)) {
            this.velocity.y += this.acceleration * dt;
        } else if(Gdx.input.isKeyPressed(Input.Keys.S)) {
            this.velocity.y -= this.acceleration * dt;
        } else {
            if(Math.abs(this.velocity.y) < .5f) {
                this.velocity.y = 0;
            } else {
                this.velocity.y += -Math.signum(this.velocity.y) * deceleration * dt;
            }   
        }

        if(Gdx.input.isKeyPressed(Input.Keys.A)) {
            this.velocity.x -= this.acceleration * dt;
        } else if(Gdx.input.isKeyPressed(Input.Keys.D)) {
            this.velocity.x += this.acceleration * dt;
        } else {
            if(Math.abs(this.velocity.x) < .5f) {
                this.velocity.x = 0;
            } else {
                this.velocity.x += -Math.signum(this.velocity.x) * deceleration * dt;
            }  
        }

        float speedMult = Math.min(this.trueMaxSpeed, Math.max(.8f * (maxChronite / numChronite), maxSpeed));
        if(Math.abs(this.velocity.len()) > speedMult) {
            this.velocity.nor().scl(speedMult);
        } 
        this.playerSprite.translate(this.velocity.x * dt, this.velocity.y * dt);


        if(Gdx.input.isButtonPressed(Input.Buttons.LEFT) ||
            Gdx.input.isButtonPressed(Input.Buttons.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.R)) {
            spawnBullet(dt, worldViewport);
        }

    }

    private void logic(float dt, Viewport worldViewport) {
        float playerX = playerSprite.getX() + playerSprite.getWidth() / 2f;
        float playerY = playerSprite.getY() + playerSprite.getHeight() / 2f;

        // Get current movement direction
        Vector2 movementDir = new Vector2(velocity.x, velocity.y);
        Vector2 offset = new Vector2(movementDir).nor().scl((float)-Math.max(Math.sqrt((double)movementDir.len2()/5f), .1)).scl(.015f);
        if(Math.abs(offset.len()) > .025f) {
            offset.nor().scl(.025f);
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
        if(this.freezeTimeCooldownEndTime < System.currentTimeMillis()) {
            if(numChronite <= this.freezeTimeTrigger && this.freezeTimeEndTime == -1 && maxChronite >= this.minChroniteToFreezeTime) {
                Globals.freezeTime = true;
                this.freezeTimeEndTime = System.currentTimeMillis() + this.freezeTimeMaxTime;
                maxChronite /= 1.2f;
                this.timeFreezeEffect.play(Globals.musicAudioLevel);
                Globals.gamePlayTrack.pause();
            } 
            this.isFreezeTimeCooldownActive = false;
        } 
        if(System.currentTimeMillis() > this.freezeTimeEndTime && Globals.freezeTime) {
                Globals.freezeTime = false;
                this.freezeTimeEndTime = -1;
                this.freezeTimeCooldownEndTime = System.currentTimeMillis() + freezeTimeCooldownTime;
                Globals.gamePlayTrack.play();
                this.timeFreezeEffect.stop();
                this.isFreezeTimeCooldownActive = true;
            }
        

        //checkCollisions();
    }

    private void checkCollisions() {
        for(Collider collider : Globals.colliders) {
            if(collider.active) {
                if(collider.name.contains("Tier")) {
                    
                } 
            }
        }
    }

    private void render(float dt, float[] worldSize, SpriteBatch spriteBatch, Viewport worldViewport) {
        float chroniteRatioSmall = (numChronite / maxChronite);
        float chroniteRatioLarge = (maxChronite / numChronite);

        if(!(Globals.freezeTime || this.isFreezeTimeCooldownActive)) {
            float ratio = (maxChronite) / minChroniteToFreezeTime;
            if(ratio < 1) {
                ratio = 0;
            }
            this.spriteLayer2.rotate(90 * ratio * dt);
            this.spriteLayer3.rotate(-120 * ratio  * dt);
        }
        

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
            this.bulletFireSound.play(Globals.soundEffectAudioLevel);
            this.fireCooldownEndTime = System.currentTimeMillis() + this.fireCooldown;
            if(numChronite - bulletCost >= 1) {
                if(!Globals.freezeTime) {
                    numChronite -= bulletCost;
                }
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

    private void reset() {
        this.setPlayerStartPosition = true;
        maxChronite = 21f;
        numChronite = maxChronite;
        
        for(int i = 0; i < bulletList.size(); i++) {
            bulletList.get(i).collider.active = false;
            bulletList.remove(i);
            i--;
        }

        this.velocity = new Vector3();
        IFrameEndTime = -1;
        fireCooldownEndTime = -1;
        freezeTimeCooldownEndTime = -1;
        freezeTimeEndTime = -1;
        isFreezeTimeCooldownActive = false;
        alpha = 1f;

        this.bulletFireSound.stop();
        this.timeFreezeEffect.stop();
        
        Globals.canHitPlayer = true;
        Globals.freezeTime = false;
    }

    public float[] getVelocity() {
        return new float[] {this.velocity.x, this.velocity.y};
    }
}
