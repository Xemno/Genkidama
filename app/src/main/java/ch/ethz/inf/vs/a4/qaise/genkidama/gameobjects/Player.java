package ch.ethz.inf.vs.a4.qaise.genkidama.gameobjects;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * Created by Qais on 04-Nov-17.
 */

//TODO: you might want to modify this class

public class Player implements GameObject {

    //    private Rect player;
    private String name;
    private int currentHealth, maxHealth, strength, skill, defence, range, movement;

    private Rect rectangle; // for collision detection
    private int color;

    public Player() {
        // TODO: create player and set invisible rectangle around for collision detection
    }

    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint(); // The Paint class holds the style and color information about how to draw geometries, text and bitmaps
        paint.setColor(color);
        canvas.drawRect(rectangle, paint);
    }

    @Override
    public void update() {
        // you can leave this empty if you dont need it
    }

    public void update(Point point) {
        /* TODO: Update position
         *  update the position of the player and surround it with a rectangle
         *  for collision detection, i.e., rectangle moves with the player.
          */
    }

    public Rect getRectangle() {
        return rectangle; // for collision detection
    }

}
