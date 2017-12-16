package ch.ethz.inf.vs.a4.qaise.genkidama.engine;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

import ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel;


/**
 * This is the game loop, which is the main thread
 * that updates the screen and draws to the screen.
 * So all the updating and drawing is in finally done here.
 */

public class GameEngine extends Thread{

    public static final String TAG = "###MainThread### -> ";
    public static final int MAX_FPS = 30;   // limit FPS, otherwise too many unnecessary calls to the game loop
    private double averageFPS;
    private SurfaceHolder surfaceHolder;
    private GamePanel gamePanel;
    private boolean running; // if you want to make this public then make it volatile!
    public static Canvas canvas;

    public GameEngine(SurfaceHolder surfaceHolder, GamePanel gamePanel) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gamePanel = gamePanel;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        long startTime;
        long timeMillis = 1000/MAX_FPS;
        long waitTime;
        int frameCount = 0;
        long totalTime = 0;
        long targetTime = 1000/MAX_FPS;

        while (running) {
            startTime = System.nanoTime();
            canvas = null;

//            this.gamePanel.update(); <-- MAYBE MOVE IT HERE, BETTER

            try {
                if (this.surfaceHolder.getSurface().isValid()) {
                    canvas = this.surfaceHolder.lockCanvas();   // locks the canvas and start editing the pixels in the surface.
                    synchronized (surfaceHolder) {
                        this.gamePanel.update();    // updates the game objects. CAN BE UPDATED OUTSIDE THIS BLOCK!
                        this.gamePanel.draw(canvas);    // draws the updated game objects
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    try {
                        this.surfaceHolder.unlockCanvasAndPost(canvas);  // releases the canvas
                    } catch (Exception e) {e.printStackTrace();}

                }
            }
            timeMillis = (System.nanoTime() - startTime)/1000000; // elapsed time, divide by 1M to get ms
            waitTime = targetTime - timeMillis;
            try {
                if (waitTime > 0) { // if we finished the frame earlier than the targetTime
                    this.sleep(waitTime);
                }
            } catch (Exception e) {e.printStackTrace();}

            totalTime += System.nanoTime() - startTime;
            frameCount++;

            if (frameCount == MAX_FPS) {
                averageFPS = 1000/((totalTime/frameCount)/1000000);
                // reset all the values
                frameCount = 0;
                totalTime = 0;
            }
        }
    }
}
