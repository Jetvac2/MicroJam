package com.Jetvac2.MicroJam.Player;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;

public class ChroniteManager {
    private static ArrayList<Chronite> chroniteList = new ArrayList<Chronite>();
    private static int toSpawn = 0;
    private static float[] spawnPoint;
    private static int spawnPerFrame = 1;
    public static void spawnChronite(int number, float[] spawnOrgin) {
        number += ((int)(Math.random() * (number-1) * 2)) - (number-1);
        toSpawn += number;
        spawnPoint = spawnOrgin;
    }

    public static void updateChronite(float dt, SpriteBatch spriteBatch) {
        for(int i = 0; i < Math.min(spawnPerFrame, toSpawn); i++) {
            toSpawn--;
            chroniteList.add(new Chronite(spawnPoint));
        }

        for(int i = 0; i < chroniteList.size(); i++) {
            Chronite chronite = chroniteList.get(i);
            if(chronite.collected) {
                chronite.collider.active = false;
                chroniteList.remove(i);
                i-=1;
            } else {
                chronite.updateChronite(dt, spriteBatch);
            }
        }
    }
}
