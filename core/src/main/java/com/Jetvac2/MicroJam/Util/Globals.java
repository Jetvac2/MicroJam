package com.Jetvac2.MicroJam.Util;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class Globals {
    public static ArrayList<Collider> colliders = new ArrayList<Collider>();
    public static boolean canHitPlayer = true;
    public static boolean gameGoing = true;
    public static boolean freezeTime = false;
    public static Music gamePlayTrack;

    public static float soundEffectAudioLevel = 1.0f;
    public static float musicAudioLevel = .1f;
    public static boolean musicStarted = false;
    public static boolean bulletExplodeEffectPrepped = false;
    public static boolean bulletFireEffectPrepped = false;
    public static boolean playerDamageEffectPrepped = false;

    public static Music bulletExplodingSoundEffect;

    public static void initBulletSound() {
        bulletExplodingSoundEffect = Gdx.audio.newMusic(Gdx.files.internal("exploding.mp3"));
    }


}
