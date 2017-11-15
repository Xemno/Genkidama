package ch.ethz.inf.vs.a4.qaise.genkidama.gameobjects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants;

// TODO: Create and draw the ground here, or just draw a picture

public class BaseFloor implements GameObject {
    private int topDistance;         // distance ceiling floor (y Value for floor top)
    private final int FLOOR_COLOR = Color.rgb(160, 120, 50);
    private Rect floor;

    public BaseFloor(int heightPercent){
        topDistance = (100 - heightPercent)*Constants.SCREEN_HEIGHT/100;

        floor = new Rect(0, topDistance, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
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

    public void update(int heightPercent) {
        topDistance = (100 - heightPercent)*Constants.SCREEN_HEIGHT/100;
        floor.top = topDistance;
    }
}
