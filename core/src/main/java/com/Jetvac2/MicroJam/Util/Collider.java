package com.Jetvac2.MicroJam.Util;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;

public class Collider {
    public Polygon colliderPoly;
    public String name;
    
    public Collider(Polygon collider, String name) {
        this.colliderPoly = collider;
        this.name = name;
    }



}
