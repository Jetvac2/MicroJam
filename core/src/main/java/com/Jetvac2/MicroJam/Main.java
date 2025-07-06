package com.Jetvac2.MicroJam;

import com.Jetvac2.MicroJam.Enemies.EnemyManager;
import com.Jetvac2.MicroJam.Player.ChroniteManager;
import com.Jetvac2.MicroJam.Player.Player;
import com.Jetvac2.MicroJam.Runes.RuneManager;
import com.Jetvac2.MicroJam.UI.Menu;
import com.Jetvac2.MicroJam.Util.Globals;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Stage;
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
    private int backgroundSpiceLength = 10;
    private boolean needsResetFrame = false;
    private boolean mustReset = false;

    private Menu menu;
    private boolean firstFrame = true;

    private SpriteBatch uiBatch;
    private BitmapFont scoreDisplay;    

    GlyphLayout layout = new GlyphLayout();
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
        this.backgroundSpice.setEmittersCleanUpBlendFunction(true);

        this.scoreDisplay = new BitmapFont(Gdx.files.internal("UI/Fonts/scoreFont.fnt"));
        this.scoreDisplay.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        this.scoreDisplay.setColor(.1f, .1f, 0.15f, 1);
        this.uiBatch = new SpriteBatch(1);         
        this.menu = new Menu();
        RuneManager.init(player.getPlayerPose()[0], player.getPlayerPose()[1]);


        Globals.gamePlayTrack = Gdx.audio.newMusic(Gdx.files.internal("Music/GamePlayTrack.mp3")); 
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
            this.menu.dispose();
            Gdx.input.setInputProcessor(new Stage());
            this.worldViewport = new FitViewport(2f, 2f, new OrthographicCamera());
            this.worldViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        }

        this.worldViewport.getCamera().update();;
        float[] worldSize = new float[] {this.worldViewport.getWorldWidth(), this.worldViewport.getWorldHeight()};
        
        float[] playerPose = this.player.getPlayerPose();
        float[] playerSize = this.player.getPlayerSize();

        this.backgroundSpice.setPosition(playerPose[0]+playerSize[0]/2, playerPose[1]+playerSize[1]/2);
        this.backgroundRenderer.setProjectionMatrix(this.worldViewport.getCamera().combined);

      
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
        float[] backgroundColor = new float[] {.4f + (.6f * chroniteRatioSmall), .07f + (.33f * chroniteRatioSmall), 1f - (.9f * chroniteRatioSmall)};
        Globals.backgroundColor = backgroundColor;
        this.backgroundRenderer.setColor(backgroundColor[0], backgroundColor[1], backgroundColor[2], 1f);
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

        this.backgroundSpiceBatch.setProjectionMatrix(this.worldViewport.getCamera().combined);
        this.backgroundSpiceBatch.begin();
        this.backgroundSpice.draw(this.backgroundSpiceBatch);
        RuneManager.update(dt, playerPose[0], playerPose[1], backgroundSpiceBatch);
        ChroniteManager.updateChronite(dt, this.backgroundSpiceBatch);
        this.backgroundSpiceBatch.end();
        // Render Player
        this.player.updatePlayer(dt, worldViewport, worldSize,
            this.playerBatch);

        this.enemyBatch.setProjectionMatrix(this.worldViewport.getCamera().combined);
        this.enemyBatch.begin();
        EnemyManager.updateEnemies(dt, worldSize, enemyBatch, playerPose, playerSize);
        this.enemyBatch.end();
        

        this.uiBatch.setProjectionMatrix(this.worldViewport.getCamera().combined);
        this.scoreDisplay.setUseIntegerPositions(false);
        this.scoreDisplay.getData().setScale(.00025f);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_DST_COLOR, GL20.GL_ZERO);


        if(Globals.freezeTime) {
            this.backgroundRenderer.begin(ShapeType.Filled);
            this.backgroundRenderer.setColor(.6f, .6f, .5f, 1f);
            this.backgroundRenderer.rect(playerPose[0] - worldSize[0]/2 + playerSize[0]/2 , playerPose[1] - worldSize[1]/2 + playerSize[1]/2, worldSize[0], worldSize[1]);
            this.backgroundRenderer.end();
        }
        
        uiBatch.begin();
        
        layout.setText(scoreDisplay, "Score:" + (int)Globals.score);
        scoreDisplay.draw(uiBatch, layout, (-layout.width / 2f) + playerPose[0] + .4f, (layout.height / 2f) + playerPose[1] + 1f);

        uiBatch.end();
        
    }
}