package com.Jetvac2.MicroJam;

import com.Jetvac2.MicroJam.Enemies.EnemyManager;
import com.Jetvac2.MicroJam.Player.ChroniteManager;
import com.Jetvac2.MicroJam.Player.Player;
import com.Jetvac2.MicroJam.UI.Menu;
import com.Jetvac2.MicroJam.Util.Globals;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Main implements ApplicationListener {
    private ShapeRenderer backgroundRenderer;
    private SpriteBatch playerBatch;
    private SpriteBatch enemyBatch;
    private Player player;
    private FitViewport worldViewport;

    private ParticleEffect backgroundSpice;
    private SpriteBatch backgroundSpiceBatch;
    private int backgroundSpiceLength = 5;
    private boolean needsResetFrame = false;
    private boolean mustReset = false;

    private Menu menu;
    private boolean firstFrame = true;
    

    @Override
    public void create() {
        Gdx.graphics.setForegroundFPS(120);
        this.worldViewport = new FitViewport(2f, 2f, new OrthographicCamera());
        this.backgroundRenderer = new ShapeRenderer();
        this.playerBatch = new SpriteBatch();
        this.enemyBatch = new SpriteBatch();
        this.player = new Player();
        this.backgroundSpiceBatch = new SpriteBatch();

        this.backgroundSpice = new ParticleEffect();
        this.backgroundSpice.loadEmitters(Gdx.files.internal("ParticalEffect/BackgroundSpice.p"));
        this.backgroundSpice.loadEmitterImages(Gdx.files.internal("ParticalEffect/"));
        this.backgroundSpice.setDuration(backgroundSpiceLength);
        this.backgroundSpice.scaleEffect(.1f);

        this.menu = new Menu();

        Globals.gamePlayTrack = Gdx.audio.newMusic(Gdx.files.internal("Music/GamePlayTrack.mp3"));
        Globals.gamePlayTrack.setLooping(true);
        Globals.gamePlayTrack.setVolume(Globals.musicAudioLevel);
        Globals.gamePlayTrack.play();
    
    }

    @Override
    public void resize(int width, int height) {
        if(Globals.gameGoing) {
            this.worldViewport.update(width, height, true);
        } else {
            this.menu.resize(width, height);
        }
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        initSound();
        
        if(Globals.gameGoing || (needsResetFrame && mustReset)) {
            ScreenUtils.clear(Color.BLACK);
            this.needsResetFrame = false;
            if(Globals.gameGoing) {
                mustReset = true;

            } else {
                mustReset = false;
            }

            updateGame(dt);
        } else {
            if(mustReset) {
                needsResetFrame = true;
        
            } else {
                this.firstFrame = true;
                this.menu.render(dt);
            }
        }

      
    }

    @Override
    public void pause() {
        if(Globals.gameGoing) {

        } else {
            this.menu.pause();
        }
        
    }

    @Override
    public void resume() {
        if(Globals.gameGoing) {

        } else {
            this.menu.resume();
        }
    }

    @Override
    public void dispose() {
        this.menu.dispose();
    }

    private void initSound() {
        if (Gdx.input.justTouched() && !Globals.musicStarted) {
            Globals.gamePlayTrack.setVolume(Globals.musicAudioLevel);
            Globals.gamePlayTrack.setLooping(true);
            Globals.gamePlayTrack.play();
            Globals.musicStarted = true;
        }

        if(Gdx.input.justTouched() && !Globals.bulletExplodeEffectPrepped) {
            Globals.initBulletSound();
            Globals.bulletExplodingSoundEffect.setVolume(0);
            Globals.bulletExplodingSoundEffect.setLooping(false);
            Globals.bulletExplodingSoundEffect.play();
            Globals.bulletExplodeEffectPrepped = true;
        }

        if (!Globals.gamePlayTrack.isPlaying()) {
            Globals.gamePlayTrack.setLooping(true);
            Globals.gamePlayTrack.setVolume(Globals.musicAudioLevel);
            Globals.gamePlayTrack.play();
        }

    }

    private void updateGame(float dt) {
        if(this.firstFrame) { 
            this.worldViewport = new FitViewport(2f, 2f, new OrthographicCamera());
            this.worldViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        }
        float[] worldSize = new float[] {this.worldViewport.getWorldWidth(), this.worldViewport.getWorldHeight()};
        
        float[] playerPose = this.player.getPlayerPose();
        float[] playerSize = this.player.getPlayerSize();
        this.backgroundSpice.setPosition(playerPose[0]+playerSize[0]/2, playerPose[1]+playerSize[1]/2);
        this.backgroundRenderer = new ShapeRenderer();
        this.backgroundRenderer.setProjectionMatrix(this.worldViewport.getCamera().view);
        this.backgroundRenderer.begin(ShapeRenderer.ShapeType.Filled);

        if(this.firstFrame) {
            this.firstFrame = false;
            this.backgroundSpice.start();
        }

        if(!this.backgroundSpice.isComplete()) {
            this.backgroundSpice.update(dt);
        } else {
            this.backgroundSpice.reset(false);
            this.backgroundSpice.start();
        }

        // Oragne to greenish purpole
        float chroniteRatioSmall = Player.numChronite / Player.maxChronite;
        this.backgroundRenderer.setColor(.4f + (.6f * chroniteRatioSmall), .07f + (.33f * chroniteRatioSmall), 1f - (.9f * chroniteRatioSmall), 1f);
        for (ParticleEmitter emitter : backgroundSpice.getEmitters()) {
            // Get the number of timeline entries
            int timelineSize = emitter.getTint().getTimeline().length;

            // Create a color array with 3 floats per entry (RGB)
            float[] colors = new float[timelineSize * 3];

            // Fill with your desired color (e.g., blue)
            for (int i = 0; i < timelineSize; i++) {
                colors[i * 3] = .5f + (.5f * chroniteRatioSmall);   
                colors[i * 3 + 1] = .075f + (.25f * chroniteRatioSmall); // G
                colors[i * 3 + 2] = .8f - (.7f * chroniteRatioSmall); // B
            }

            emitter.getTint().setColors(colors);
        }


        this.backgroundRenderer.rect(playerPose[0] - worldSize[0]/2 + playerSize[0]/2 , playerPose[1] - worldSize[1]/2 + playerSize[1]/2, worldSize[0], worldSize[1]);
        this.backgroundRenderer.end();

        this.backgroundSpiceBatch.setProjectionMatrix(this.worldViewport.getCamera().view);
        this.backgroundSpiceBatch.begin();
        this.backgroundSpice.draw(this.backgroundSpiceBatch);
        ChroniteManager.updateChronite(dt, this.backgroundSpiceBatch);
        this.backgroundSpiceBatch.end();
        // Render Player
        this.player.updatePlayer(dt, worldViewport, worldSize,
            this.playerBatch);

        this.enemyBatch.setProjectionMatrix(this.worldViewport.getCamera().view);
        this.enemyBatch.begin();
        EnemyManager.updateEnemies(dt, worldSize, enemyBatch, playerPose, playerSize);
        this.enemyBatch.end();
    }
}