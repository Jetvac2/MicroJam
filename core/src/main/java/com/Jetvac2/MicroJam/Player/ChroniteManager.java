package com.Jetvac2.MicroJam.Player;

import java.util.ArrayList;

import com.Jetvac2.MicroJam.Util.Globals;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ChroniteManager {
    private static boolean inStartState = true;
    private static ArrayList<Chronite> chroniteList = new ArrayList<Chronite>();
    private static int toSpawn = 0;
    private static float[] spawnPoint;
    private static int spawnPerFrame = 1;
    private static float maxChroniteAge = 10f;
    private static int maxChroniteNum = 40;
    
    public static void spawnChronite(int number, float[] spawnOrgin) {
        int numberMod = ((int)(Math.random() * 4f)) - 2;
        toSpawn += number + numberMod;
        spawnPoint = spawnOrgin;
    }

    public static void updateChronite(float dt, SpriteBatch spriteBatch) {
        if(Globals.gameGoing) {
            inStartState = false;
            for(int i = 0; i < Math.min(spawnPerFrame, toSpawn); i++) {
                toSpawn--;
                chroniteList.add(new Chronite(spawnPoint));
                if(chroniteList.size() >= maxChroniteNum) {
                    chroniteList.remove(i);
                }
            }

            for(int i = 0; i < chroniteList.size(); i++) {
                Chronite chronite = chroniteList.get(i);
                if(!Globals.freezeTime) {
                    chronite.age += dt;
                }
                
                if(chronite.collected || chronite.age > maxChroniteAge) {
                    chronite.collider.active = false;
                    chroniteList.remove(i);
                    i-=1;
                } else {
                    chronite.updateChronite(dt, spriteBatch);
                }
            }
        } else if(!inStartState) {
            reset();
        }
    }

    private static void reset() {
        inStartState = true;
        toSpawn = 0;
        spawnPoint = new float[2];
        for(int i = 0; i < chroniteList.size(); i++) {
            chroniteList.get(i).collider.active = false;
            chroniteList.remove(i);
            i--;
        }
    }
}
