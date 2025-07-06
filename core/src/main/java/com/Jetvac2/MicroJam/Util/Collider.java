package com.Jetvac2.MicroJam.Util;

import com.badlogic.gdx.math.Polygon;

public class Collider {
    public Polygon colliderPoly;
    public String name;
    public float[] data;
    public boolean active = true;
    
    public Collider(Polygon collider, String name) {
        this.colliderPoly = collider;
        this.name = name;
        data = new float[] {};
    }

    public Collider(Polygon collider, String name, float[] data) {
        this.colliderPoly = collider;
        this.name = name;
        this.data = data;
    }

}
