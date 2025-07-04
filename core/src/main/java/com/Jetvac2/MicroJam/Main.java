package com.Jetvac2.MicroJam;

import com.Jetvac2.MicroJam.Enemies.BaseEnemy;
import com.Jetvac2.MicroJam.Enemies.EnemyManager;
import com.Jetvac2.MicroJam.Player.Chronite;
import com.Jetvac2.MicroJam.Player.ChroniteManager;
import com.Jetvac2.MicroJam.Player.Player;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Main implements ApplicationListener {
    private ShapeRenderer backgroundRenderer;
    private SpriteBatch playerBatch;
    private SpriteBatch enemyBatch;
    private Player player;
    private FitViewport worldViewport;

    private ParticleEffect backgroundSpice;
    private int backgroundSpiceLength = 5;

    @Override
    public void create() {
        this.worldViewport = new FitViewport(2f, 2f, new OrthographicCamera());
        this.backgroundRenderer = new ShapeRenderer();
        this.playerBatch = new SpriteBatch();
        this.enemyBatch = new SpriteBatch();
        this.player = new Player();

        this.backgroundSpice = new ParticleEffect();
        this.backgroundSpice.loadEmitters(Gdx.files.internal("ParticalEffect/BackgroundSpice.p"));
        this.backgroundSpice.loadEmitterImages(Gdx.files.internal("ParticalEffect/"));
        this.backgroundSpice.setDuration(backgroundSpiceLength);
        this.backgroundSpice.scaleEffect(.1f);
        this.backgroundSpice.start();
        
    }

    @Override
    public void resize(int width, int height) {
        this.worldViewport.update(width, height, true);
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        ScreenUtils.clear(Color.BLACK);
        System.out.println(dt);
        float[] worldSize = new float[] {this.worldViewport.getWorldWidth(), this.worldViewport.getWorldHeight()};
        this.worldViewport.apply();
        float[] playerPose = this.player.getPlayerPose();
        float[] playerSize = this.player.getPlayerSize();
        this.backgroundSpice.setPosition(playerPose[0]+playerSize[0]/2, playerPose[1]+playerSize[1]/2);
        this.backgroundRenderer = new ShapeRenderer();
        this.backgroundRenderer.setProjectionMatrix(this.worldViewport.getCamera().view);
        this.backgroundRenderer.begin(ShapeRenderer.ShapeType.Filled);

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

        // Render Player
        this.playerBatch.setProjectionMatrix(this.worldViewport.getCamera().view);
        this.playerBatch.begin();
        this.backgroundSpice.draw(this.playerBatch);
        ChroniteManager.updateChronite(dt, playerBatch);
        this.player.updatePlayer(dt, worldViewport, worldSize,
            this.playerBatch);
        this.playerBatch.end();

        this.enemyBatch.setProjectionMatrix(this.worldViewport.getCamera().view);
        this.enemyBatch.begin();
        EnemyManager.updateEnemies(dt, worldSize, enemyBatch, playerPose, playerSize);
        this.enemyBatch.end();

        backgroundRenderer.begin(ShapeType.Line);
        backgroundRenderer.setColor(dt, 1, dt, dt);
        backgroundRenderer.polygon(this.player.playerHitBox.colliderPoly.getTransformedVertices());
        backgroundRenderer.end();
    }

    @Override
    public void pause() {
        
    }

    @Override
    public void resume() {
        
    }

    @Override
    public void dispose() {

    }
}