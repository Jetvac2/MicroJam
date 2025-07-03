package com.Jetvac2.MicroJam.Util;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;

public class Collider {

    public Rectangle colliderRect;
    public Circle colliderCircle;
    public String name;
    /**
     * Make sure to do a null check on either type and use the non null one
     * @param collider
     * @param name
     */
    public Collider(Rectangle collider, String name) {
        this.colliderRect = collider;
        this.name = name;
    }

    /**
     * Make sure to do a null check on either type and use the non null one
     * @param collider
     * @param name
     */
    public Collider(Circle collider, String name) {
        this.colliderCircle = collider;
        this.name = name;
    }


}
