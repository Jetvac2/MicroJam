package com.Jetvac2.MicroJam.Util;

import java.util.ArrayList;

import com.badlogic.gdx.audio.Music;

public class Globals {
    public static ArrayList<Collider> colliders = new ArrayList<Collider>();
    public static boolean canHitPlayer = true;
    public static boolean gameGoing = true;
    public static boolean freezeTime = false;
    public static Music gamePlayTrack;

    public static float soundEffectAudioLevel = 1.0f;
    public static float musicAudioLevel = .5f;
}
