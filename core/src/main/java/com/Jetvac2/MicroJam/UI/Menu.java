package com.Jetvac2.MicroJam.UI;

import java.util.Comparator;

import com.Jetvac2.MicroJam.Util.Globals;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Menu {
    private Stage stage;

    private Label title;
    private Label lore;
    private String loreCont = "     A man became obsessed with living forever, not for love of life but for fear of death. " +
                "In pursuit of immortality, he became a chronomancer(Time Mage). From his fear and obsession stemmed great power, " +
                "he became the strongest mage ever known. However, achieving eternal life on Earth proved impossible. " + 
                "When he was so close to death he could barely walk, he became infatuated with a gem known as Chronite, solidified time mana. " + 
                "Over years, the Chronite infected his mind and his thread of time until the Universal Timeline rejected his thread and " + 
                "banished him into the Splintered Continuum, an infinite waste land of time remnants and things incomprehensible to the human mind. " + 
                "This was the cost of his actions. In the Splintered Continuum the  chronomancer is desperately clinging to life, fighting for every second. " +
                "To stabilize his mortal form in the Splintered Continuum requires a constant supply of Chronite.\n\n" + 
                "Gameplay: Every action the player takes costs Chronite. To live, the player must kill the enemies and consume the Chronite they drop.\n" +
                "However, the lower on chronite the player is the more powerfull they are.\n\n" + //
                "    How Long Can You Last?";
    private Label musicVolumeLabel;
    private Label soundEffectVolumeLabel;
    private TextButton startGame;
    private Slider musicVolume;
    private Slider soundEffectVolume;
    Container<Slider> musicVolumeCon;
    Container<Slider> soundEffectVolumeCon;
    private Vector2 worldSize;

    private Label scoreBoard;
    private int numScores;
    
    public Menu() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("UI/Fonts/Roboto-Black.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 500;
        BitmapFont buttonFont = generator.generateFont(parameter);
        BitmapFont lableFont = generator.generateFont(parameter);
        generator.dispose();
        Skin skin = new Skin();
        skin.add("ButtonFont", buttonFont);
        skin.add("LabalFont", lableFont);
        skin.addRegions(new TextureAtlas(Gdx.files.internal("UI/Skins/UI.atlas")));
        skin.load(Gdx.files.internal("UI/Skins/UI.json"));
        this.stage = new Stage(new FitViewport(2500f, 2000f));
        Gdx.input.setInputProcessor(stage);

        this.title = new Label("Splintered Continuum", skin);
        this.title.setPosition(0, 0);
        this.title.setFontScale(.4f);
        this.lore = new Label(loreCont, skin);
        this.lore.setWrap(true);
        this.lore.setFontScale(.08f);
        this.musicVolumeLabel = new Label("Music Volume", skin);
        this.musicVolumeLabel.setFontScale(.25f);
        this.soundEffectVolumeLabel = new Label("SFX Volume", skin);
        this.soundEffectVolumeLabel.setFontScale(.25f);
        this.startGame = new TextButton("Start Game", skin);
        this.startGame.getLabel().setFontScale(.3f);
        this.musicVolume = new Slider(0, 1, .01f, false, skin) {
            @Override
            public float getPrefWidth() {
                return 2000f; 
            }
        };
        this.soundEffectVolume = new Slider(0, 1, .01f, false, skin) { @Override
            public float getPrefWidth() {
                return 2000f;
            };
        };

        musicVolumeCon = new Container<Slider>(musicVolume);
        musicVolumeCon.setTransform(true);
        musicVolumeCon.setScale(.3f);
        musicVolumeCon.getActor().setValue(.25f);
        soundEffectVolumeCon = new Container<Slider>(soundEffectVolume);
        soundEffectVolumeCon.setTransform(true);
        soundEffectVolumeCon.setScale(.3f);
        soundEffectVolumeCon.getActor().setValue(1f);

        this.scoreBoard = new Label(genScoreBoard(), skin);
        this.scoreBoard.setFontScale(.25f);
        this.scoreBoard.setWrap(true);

        stage.addActor(this.title);
        stage.addActor(this.lore);
        stage.addActor(this.musicVolumeLabel);
        stage.addActor(this.soundEffectVolumeLabel);
        stage.addActor(this.scoreBoard);
        stage.addActor(this.startGame);
        stage.addActor(this.soundEffectVolumeCon);
        stage.addActor(this.musicVolumeCon);

        addListeners();
    }

    private CharSequence genScoreBoard() {
        String scoreBoard = "High Scores:";
        Globals.scores.sort(Comparator.reverseOrder());

        for(int i = 0; i < 5; i++) {
            float rawScore;
            try {
                rawScore = Globals.scores.get(i);
            } catch (Exception e) {
                rawScore = 0;
            }
            int roundedScore = Math.round(rawScore);
            scoreBoard += "\n" + (i+1) + ": " + roundedScore;
        }
        return scoreBoard;
    }

    private void addListeners() {
        this.musicVolume.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                Globals.gamePlayTrack.setVolume(((Slider)actor).getValue());
            }
        });

        this.soundEffectVolume.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
               Globals.soundEffectAudioLevel = (((Slider)actor).getValue());
            }
        });

        this.startGame.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                Globals.gameGoing = true;
            }
        });
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void render(float dt) {
        ScreenUtils.clear(.1f, .1f, 0.15f, 1);

        if(Globals.scores.size() == this.numScores) {
            this.scoreBoard.setText(genScoreBoard());
            this.numScores++;
        }

        this.worldSize = new Vector2(stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
        this.title.setPosition(worldSize.x/2 - worldSize.x/2.5f, worldSize.y - worldSize.y/15);
        this.title.setSize(.15f, .15f);
        this.lore.setPosition(0, 0);
        this.lore.setSize(worldSize.x/3.75f , worldSize.y - worldSize.y/12);
        this.startGame.setSize(worldSize.x/3f, worldSize.y/10);
        this.startGame.setPosition(worldSize.x/2f - worldSize.x/5.5f, worldSize.y - worldSize.y/3.25f);
        this.musicVolumeLabel.setPosition(worldSize.x/1.935f - worldSize.x/5.5f, worldSize.y - worldSize.y/1.85f);
        this.musicVolumeCon.setPosition(worldSize.x/2f - worldSize.x/21, worldSize.y - worldSize.y/2.15f);
        this.soundEffectVolumeLabel.setPosition(worldSize.x/1.935f - worldSize.x/5.5f, worldSize.y - worldSize.y/1.45f);
        this.soundEffectVolumeCon.setPosition(worldSize.x/2f - worldSize.x/21, worldSize.y - worldSize.y/1.625f);
        this.scoreBoard.setPosition(worldSize.x/1.4f, -worldSize.y/3.125f);

        stage.act(dt);
        stage.draw();
    }

    public void pause() {
        
    }

    public void resume() {
        
    }

    public void dispose() {        
    }
}
