package com.Jetvac2.MicroJam;

import com.Jetvac2.MicroJam.Enemies.BaseEnemy;
import com.Jetvac2.MicroJam.Enemies.EnemyManager;
import com.Jetvac2.MicroJam.Player.Chronite;
import com.Jetvac2.MicroJam.Player.Player;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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
    Chronite chronite1;
    Chronite chronite2;
    Chronite chronite3;
    Chronite chronite4;

    @Override
    public void create() {
        this.worldViewport = new FitViewport(2f, 2f, new OrthographicCamera());
        this.backgroundRenderer = new ShapeRenderer();
        this.playerBatch = new SpriteBatch();
        this.enemyBatch = new SpriteBatch();
        this.player = new Player();
        chronite1 = new Chronite(new float[]{.5f, .5f});
        chronite2 = new Chronite(new float[]{.5f, .5f});
        chronite3 = new Chronite(new float[]{.5f, .5f});
        chronite4 = new Chronite(new float[]{.5f, .5f});

    }

    @Override
    public void resize(int width, int height) {
        this.worldViewport.update(width, height, true);
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        ScreenUtils.clear(Color.BLACK);
        float[] worldSize = new float[] {this.worldViewport.getWorldWidth(), this.worldViewport.getWorldHeight()};
        this.worldViewport.apply();
        float[] playerPose = this.player.getPlayerPose();
        float[] playerSize = this.player.getPlayerSize();

        this.backgroundRenderer = new ShapeRenderer();
        this.backgroundRenderer.setProjectionMatrix(this.worldViewport.getCamera().view);
        this.backgroundRenderer.begin(ShapeRenderer.ShapeType.Filled);
        this.backgroundRenderer.setColor(.506f, .129f, 1f, 1);
        this.backgroundRenderer.rect(playerPose[0] - worldSize[0]/2 + playerSize[0]/2 , playerPose[1] - worldSize[1]/2 + playerSize[1]/2, worldSize[0], worldSize[1]);
        this.backgroundRenderer.end();

        // Render Player
        this.playerBatch.setProjectionMatrix(this.worldViewport.getCamera().view);
        this.playerBatch.begin();
        this.player.updatePlayer(dt, worldViewport, worldSize,
            this.playerBatch);
        this.playerBatch.end();

        this.enemyBatch.setProjectionMatrix(this.worldViewport.getCamera().view);
        this.enemyBatch.begin();
        //EnemyManager.updateEnemies(dt, worldSize, enemyBatch, playerPose, playerSize);
        this.chronite1.updateChronite(dt, enemyBatch);
        this.chronite2.updateChronite(dt, enemyBatch);
        this.chronite3.updateChronite(dt, enemyBatch);
        this.chronite4.updateChronite(dt, enemyBatch);
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