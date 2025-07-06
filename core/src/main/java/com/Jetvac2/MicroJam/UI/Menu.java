package com.Jetvac2.MicroJam.UI;

import java.util.Comparator;

import com.Jetvac2.MicroJam.Util.Globals;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Menu {
    private Stage stage;

    private Label title;
    private Label lore;
private String loreCont = "     A man, terrified of death, became a chronomancer(Time Mage) in pursuit of immortality. His obsession granted him immense power, but eternal life remained out of reach. " +
                        "Near death, he discovered a gem, Chronite(solidified time mana), which slowly warped his mind and timeline. Rejected by the Universal Timeline, he was forced into the Splintered Continuum: " +
                        "a wasteland of time remnants and incomprehensible horrors. There, his body twisted into the likeness of the geometric abominations that dwell within. " +
                        "Desperate to survive, he slaughters the profane constructs and absorbs their Chronite to extend his fleeting life.\n\n" + 
                        "Gameplay: Every action costs Chronite. Kill enemies to collect more. The less Chronite you have, the stronger you become. Near depletion, time freezes briefly, " +
                        "but this permanently lowers your max Chronite.(If you get hit while low on Chronite you will still die, the time freeze ONLY tirggers from time and action bassed depletion)\n\n" + 
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

    private boolean firstFrame = true;
    
    public Menu() {
        BitmapFont buttonFont = new BitmapFont(Gdx.files.internal("UI/Fonts/mainFont.fnt"));
        BitmapFont lableFont = new BitmapFont(Gdx.files.internal("UI/Fonts/mainFont.fnt"));
        Skin skin = new Skin();
        skin.add("ButtonFont", buttonFont);
        skin.add("LabalFont", lableFont);
        skin.addRegions(new TextureAtlas(Gdx.files.internal("UI/Skins/UI.atlas")));
        skin.load(Gdx.files.internal("UI/Skins/UI.json"));
        this.stage = new Stage(new FitViewport(2500f, 2000f));
        Gdx.input.setInputProcessor(new Stage());

        this.title = new Label("Splintered Continuum", skin);
        this.title.setPosition(0, 0);
        this.title.setFontScale(.4f);
        this.lore = new Label(loreCont, skin);
        this.lore.setWrap(true);
        this.lore.setFontScale(.09f);
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
        musicVolumeCon.getActor().setValue(.5f);
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

        if(firstFrame) {
            Gdx.input.setInputProcessor(stage);
            stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
            this.firstFrame = false;
        }

        this.worldSize = new Vector2(stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
        this.title.setPosition(worldSize.x/2 - worldSize.x/2.95f, worldSize.y - worldSize.y/15);
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
        this.firstFrame = true;
    }
}
