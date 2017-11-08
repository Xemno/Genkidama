package ch.ethz.inf.vs.a4.qaise.genkidama.gameobjects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants;

/**
 * Created by Qais on 06-Nov-17.
 */

//TODO: you might want to modify this class

public class HealthBar implements GameObject {


    private Player player; // player associated with this healthbar
    private Rect rectangle1; // health rectangle r=[0-100%]
    private Rect rectangle2; // missing health rectangle 1-r
    private int color1; // color for health r
    private int color2; // color for missing health 1-r
    private int alphaValue1 = 200; // [0...255] // actual health
    private int alphaValue2 = 100; // [0...255] // missing health
    private Point point;

    public HealthBar() {
    }

    public HealthBar(Player player, Point point) {
        /*
         * We might want to draw 2 rectangles for one healthbar.
         * If the health is full then one rectangle has full length, the other 0 length.
         * If the health gets less then we fill the other rectangle with the difference.
         * Otherwise if we have only one rectangle we couldn't see the original full health of a player.
         */

    }

    @Override
    public void draw(Canvas canvas) {
        // draw health bar r
//        Paint paint = new Paint();
//        paint.setColor(color1);
//        paint.setAlpha(alphaValue1);
//        canvas.drawRect(rectangle1, paint);

        // draw missing health bar 1-r
//        paint.setColor(color2);
//        paint.setAlpha(alphaValue2);
//        canvas.drawRect(rectangle2, paint);
    }

    @Override
    public void update() {
        // can leave empty
    }

    public void update(int health) { //
        // TODO: update healthbar by adjusting the right value of rectangle1 and left value of rectangle2


    }
}
