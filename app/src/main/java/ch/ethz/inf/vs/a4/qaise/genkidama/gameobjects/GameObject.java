package ch.ethz.inf.vs.a4.qaise.genkidama.gameobjects;

import android.graphics.Canvas;

/**
 * Created by Qais on 02-Nov-17.
 */

public interface GameObject {

    /**
     * Draw Method
     *
     * @param canvas
     */
    public void draw(Canvas canvas);

    /**
     * Update Method
     */
    public void update();
}
