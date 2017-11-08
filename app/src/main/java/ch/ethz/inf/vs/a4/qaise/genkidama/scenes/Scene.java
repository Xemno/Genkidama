package ch.ethz.inf.vs.a4.qaise.genkidama.scenes;

import android.graphics.Canvas;
import android.view.MotionEvent;

/**
 * Created by Qais on 04-Nov-17.
 */

public interface Scene {
    public void update();
    public void draw(Canvas canvas);
    public void terminate();
    public void receiveTouch(MotionEvent event);
}
