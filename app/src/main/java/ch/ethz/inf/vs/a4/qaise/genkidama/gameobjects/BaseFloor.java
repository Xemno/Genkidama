package ch.ethz.inf.vs.a4.qaise.genkidama.gameobjects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.SCREEN_HEIGHT;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.SCREEN_WIDTH;

// TODO: Create and draw the ground here, or just draw a picture

public class BaseFloor implements GameObject {
    private final int FLOOR_COLOR = Color.rgb(94, 50, 37); // Color.rgb(78, 42, 22) is the darker shade;
    private Rect floor;

    public BaseFloor(int fixDist){
        floor = new Rect(0, fixDist, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    public Rect getRect() {     // höhö
        return this.floor;
    }

    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(FLOOR_COLOR);
        canvas.drawRect(floor, paint);
    }

    @Override
    public void update() {
    }

    // unnecessary? :
    public void update(int fixDist) {
        floor.top = fixDist;
    }
}
