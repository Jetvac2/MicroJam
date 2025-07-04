package com.Jetvac2.MicroJam.Player;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;

public class ChroniteManager {
    private static ArrayList<Chronite> chroniteList = new ArrayList<Chronite>();
   
    public static void spawnChronite(int number, float[] spawnOrgin) {
        number += ((int)(Math.random() * (number-1) * 2)) - (number-1);
        for(int i = 0; i < number; i++) {
            chroniteList.add(new Chronite(spawnOrgin));
        }
    }

    public static void updateChronite(float dt, SpriteBatch spriteBatch) {
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
