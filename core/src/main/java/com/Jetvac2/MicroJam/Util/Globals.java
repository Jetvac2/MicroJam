package com.Jetvac2.MicroJam.Util;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class Globals {
    public static ArrayList<Collider> colliders = new ArrayList<Collider>();
    public static boolean canHitPlayer = true;
    public static boolean gameGoing = false;
    public static boolean freezeTime = false;
    public static Music gamePlayTrack;
   

    public static float soundEffectAudioLevel = 1.0f;
    public static float musicAudioLevel = .25f;
    public static boolean musicStarted = false;
    public static boolean bulletExplodeEffectPrepped = false;
    public static boolean bulletFireEffectPrepped = false;
    public static boolean playerDamageEffectPrepped = false;

    public static Music bulletExplodingSoundEffect;

    public static ArrayList<Float> scores = new ArrayList<Float>();
    public static float score = 0f;
    public static float chroniteScoreAdd = 100f;
    public static float scoreAddPerSecond = 200f;
    public static float scoreAddPerTier1Enemy = 200f;

    public static void initBulletSound() {
        bulletExplodingSoundEffect = Gdx.audio.newMusic(Gdx.files.internal("exploding.mp3"));
    }


}
